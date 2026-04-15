using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using smiex_api.Models;
using smiex_api.DTOs; // Asegúrate de que tus DTOs estén aquí

namespace smiex_api.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class OfertesController : ControllerBase
    {
        private readonly Simex05Context _context;

        public OfertesController(Simex05Context context) { _context = context; }

        // 1. CLIENTE: OBTENER PENDIENTES
        [HttpGet("Pendientes/{clienteId}")]
        public async Task<ActionResult<IEnumerable<ComandaResumenDTO>>> GetOfertasPendientes(int clienteId)
        {
            return await ObtenerOfertasConTracking(clienteId, 1);
        }

        // 2. CLIENTE: OBTENER ACEPTADAS
        [HttpGet("Comandas/{clienteId}")]
        public async Task<ActionResult<IEnumerable<ComandaResumenDTO>>> GetComandasAceptadas(int clienteId)
        {
            return await ObtenerOfertasConTracking(clienteId, 2);
        }

        // 3. AGENTE: STATS GLOBALES
        [HttpGet("Agente/Stats")]
        public async Task<ActionResult> GetAgenteStats()
        {
            var stats = new {
                activas = await _context.Ofertes.CountAsync(o => o.EstatOfertaId == 2 && o.Active == 1),
                ofertas = await _context.Ofertes.CountAsync(o => o.EstatOfertaId == 1 && o.Active == 1)
            };
            return Ok(stats);
        }

        // 4. AGENTE: TODAS LAS COMANDAS (GLOBAL)
        [HttpGet("Agente/Recientes")]
        public async Task<ActionResult<IEnumerable<ComandaResumenDTO>>> GetComandasGlobales()
        {
            // Pasamos null en clienteId para que traiga TODO lo del sistema
            return await ObtenerOfertasConTracking(null, 2);
        }

        // 5. AGENTE: AVANZAR TRACKING
        [HttpPost("{id}/tracking")]
        public async Task<IActionResult> ActualizarTracking(int id, [FromBody] int nuevoTrackingId)
        {
            var oferta = await _context.Ofertes.FindAsync(id);
            if (oferta == null) return NotFound();

            oferta.TrackingActualId = nuevoTrackingId;
            await _context.SaveChangesAsync();
            return Ok(new { mensaje = "Tracking actualizado" });
        }

        // MÉTODO MAESTRO (Ahora soporta Agente y Cliente)
        private async Task<ActionResult<IEnumerable<ComandaResumenDTO>>> ObtenerOfertasConTracking(int? clienteId, int estadoId)
        {
            var pasos = await _context.TrackingSteps.Where(s => s.Id <= 9).OrderBy(s => s.Ordre)
                .Select(s => new TrackingStepDTO { Id = s.Id, Ordre = s.Ordre ?? 0, Nom = s.Nom }).ToListAsync();

            var query = _context.Ofertes.Include(o => o.PortOrigen).Include(o => o.PortDesti).Include(o => o.EstatOferta)
                .Where(o => o.Active == 1 && o.EstatOfertaId == estadoId);

            // Si hay ID, es cliente (filtro). Si es null, es agente (ve todo).
            if (clienteId.HasValue) query = query.Where(o => o.ClientId == clienteId.Value);

            var ofertas = await query.OrderByDescending(o => o.Id).ToListAsync();

            var resultado = ofertas.Select(o => new ComandaResumenDTO {
                Id = o.Id,
                NumPedido = o.NumPedido ?? "N/A",
                NombreOferta = o.NombreOferta,
                PuertoOrigen = o.PortOrigen?.Nom ?? "N/A",
                PuertoDestino = o.PortDesti?.Nom ?? "N/A",
                Estado = o.EstatOferta?.Estat,
                FechaEntrega = o.FechaEntrega?.ToString("dd/MM/yyyy") ?? "Pendiente",
                TrackingActualId = o.TrackingActualId,
                PasosSeguimiento = pasos
            });

            return Ok(resultado);
        }

        // 6. CLIENTE: ACEPTAR O RECHAZAR OFERTA
        [HttpPost("{id}/decidir")]
        public async Task<IActionResult> DecidirOferta(int id, [FromBody] DecisionOfertaDTO decision)
        {
            // Buscamos la oferta en la base de datos
            var oferta = await _context.Ofertes.FindAsync(id);
            if (oferta == null) return NotFound(new { mensaje = "Oferta no encontrada" });

            if (decision.Aceptada)
            {
                // Si acepta: Cambiamos el estado a 2 (Comanda Activa)
                oferta.EstatOfertaId = 2;

                // Opcional: Inicializamos el tracking en el primer paso si no tiene uno
                if (oferta.TrackingActualId == null || oferta.TrackingActualId == 0)
                {
                    oferta.TrackingActualId = 1;
                }
            }
            else
            {
                // Si rechaza: Marcamos como no activa o cambiamos a un estado de "Rechazada"
                oferta.Active = 0;
                oferta.RaoRebuig = decision.MotivoRechazo; // Asegúrate de que este campo exista en tu BD
            }

            try
            {
                await _context.SaveChangesAsync();
                return Ok(new { mensaje = decision.Aceptada ? "Oferta convertida en comanda" : "Oferta rechazada" });
            }
            catch (Exception ex)
            {
                return StatusCode(500, new { mensaje = "Error al guardar: " + ex.Message });
            }
        }   
    }
}
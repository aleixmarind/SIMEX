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

        public OfertesController(Simex05Context context)
        {
            _context = context;
        }

        // 1. OBTENER PENDIENTES
        [HttpGet("Pendientes/{clienteId}")]
        public async Task<ActionResult<IEnumerable<ComandaResumenDTO>>> GetOfertasPendientes(int clienteId)
        {
            return await ObtenerOfertasConTracking(clienteId, 1);
        }

        // 2. OBTENER ACEPTADAS
        [HttpGet("Comandas/{clienteId}")]
        public async Task<ActionResult<IEnumerable<ComandaResumenDTO>>> GetComandasAceptadas(int clienteId)
        {
            return await ObtenerOfertasConTracking(clienteId, 2);
        }

        // 4. ESTADÍSTICAS GLOBALES PARA EL AGENTE
        [HttpGet("Agente/Stats")]
        public async Task<ActionResult<Dictionary<string, int>>> GetAgenteStats()
        {
            var stats = new Dictionary<string, int>
            {
                { "activas", await _context.Ofertes.CountAsync(o => o.EstatOfertaId == 2 && o.Active == 1) },
                { "ofertas", await _context.Ofertes.CountAsync(o => o.EstatOfertaId == 1 && o.Active == 1) }
            };
            return Ok(stats);
        }

        // 5. OBTENER COMANDAS RECIENTES (GLOBAL) - Para el Dashboard del Agente
        [HttpGet("Agente/Recientes")]
        public async Task<ActionResult<IEnumerable<ComandaResumenDTO>>> GetComandasGlobales()
        {
            return await ObtenerOfertasConTracking(null, 2, 10);
        }

        // 6. ACTUALIZAR TRACKING (Para cuando el agente avance el estado)
        [HttpPost("{id}/tracking")]
        public async Task<IActionResult> ActualizarTracking(int id, [FromBody] int nuevoTrackingId)
        {
            var oferta = await _context.Ofertes.FindAsync(id);
            if (oferta == null) return NotFound();

            oferta.TrackingActualId = nuevoTrackingId;
            await _context.SaveChangesAsync();

            return Ok(new { mensaje = "Tracking actualizado correctamente" });
        }

        // 3. POST DECISIÓN
        [HttpPost("{id}/decidir")]
        public async Task<IActionResult> DecidirOferta(int id, [FromBody] DecisionOfertaDTO decision)
        {
            var oferta = await _context.Ofertes.FindAsync(id);
            if (oferta == null) return NotFound();

            oferta.EstatOfertaId = decision.Aceptada ? 2 : 3;
            if (!decision.Aceptada)
            {
                oferta.RaoRebuig = decision.MotivoRechazo;
            }

            await _context.SaveChangesAsync();
            return Ok(new { mensaje = "Decisión procesada correctamente" });
        }

        // MÉTODO CENTRALIZADO Y SEGURO
        private async Task<ActionResult<IEnumerable<ComandaResumenDTO>>> ObtenerOfertasConTracking(int? clienteId, int estadoId, int? limite = null)
        {
            // 1. Traer los pasos de tracking a memoria (1 al 9)
            var pasos = await _context.TrackingSteps
                .Where(s => s.Id <= 9)
                .OrderBy(s => s.Ordre)
                .Select(s => new TrackingStepDTO
                {
                    Id = s.Id,
                    Ordre = s.Ordre ?? 0,
                    Nom = s.Nom
                }).ToListAsync();

            // 2. Traer las ofertas
            var query = _context.Ofertes
                .Include(o => o.PortOrigen)
                .Include(o => o.PortDesti)
                .Include(o => o.EstatOferta)
                .Where(o => o.Active == 1 && o.EstatOfertaId == estadoId);

            if (clienteId.HasValue)
            {
                query = query.Where(o => o.ClientId == clienteId.Value);
            }

            query = query.OrderByDescending(o => o.Id);

            if (limite.HasValue)
            {
                query = query.Take(limite.Value);
            }

            var ofertas = await query.ToListAsync();

            var resultado = ofertas.Select(o => new ComandaResumenDTO
            {
                Id = o.Id,
                NumPedido = string.IsNullOrWhiteSpace(o.NumPedido) ? "N/A" : o.NumPedido,
                NombreOferta = o.NombreOferta,
                PuertoOrigen = o.PortOrigen?.Nom ?? "Sin Puerto",
                PuertoDestino = o.PortDesti?.Nom ?? "Sin Puerto",
                Estado = o.EstatOferta?.Estat ?? "Desconocido",
                FechaEntrega = o.FechaEntrega?.ToString("dd/MM/yyyy") ?? "Pendiente",
                TrackingActualId = o.TrackingActualId,
                PasosSeguimiento = pasos
            }).ToList();

            return Ok(resultado);
        }
    }
}
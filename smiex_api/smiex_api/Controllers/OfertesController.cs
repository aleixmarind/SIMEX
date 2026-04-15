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

        //cliente: obtener ofertas pendientes
        [HttpGet("Pendientes/{clienteId}")]
        public async Task<ActionResult<IEnumerable<ComandaResumenDTO>>> GetOfertasPendientes(int clienteId)
        {
            return await ObtenerOfertasConTracking(clienteId, 1);
        }

        //cliente: obtener ofertas aceptadas
        [HttpGet("Comandas/{clienteId}")]
        public async Task<ActionResult<IEnumerable<ComandaResumenDTO>>> GetComandasAceptadas(int clienteId)
        {
            return await ObtenerOfertasConTracking(clienteId, 2);
        }

        //agente: estadisticas de activas etc (revisar aun)
        [HttpGet("Agente/Stats")]
        public async Task<ActionResult> GetAgenteStats()
        {
            var stats = new {
                activas = await _context.Ofertes.CountAsync(o => o.EstatOfertaId == 2 && o.Active == 1),
                ofertas = await _context.Ofertes.CountAsync(o => o.EstatOfertaId == 1 && o.Active == 1)
            };
            return Ok(stats);
        }

        //agente: muestra todas las comandas que hay en global al agente
        [HttpGet("Agente/Recientes")]
        public async Task<ActionResult<IEnumerable<ComandaResumenDTO>>> GetComandasGlobales()
        {
            // el null para que no filtre y salgan todas las ofertas/comandas
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

        // (Agente y Cliente)
        private async Task<ActionResult<IEnumerable<ComandaResumenDTO>>> ObtenerOfertasConTracking(int? clienteId, int estadoId)
        {
            // 1. Conseguir los pasos (Lista normal)
            var pasos = await _context.TrackingSteps
                .Where(s => s.Id <= 9)
                .OrderBy(s => s.Ordre)
                .ToListAsync();

            var listaPasosDTO = new List<TrackingStepDTO>();
            foreach (var p in pasos)
            {
                listaPasosDTO.Add(new TrackingStepDTO
                {
                    Id = p.Id,
                    Ordre = p.Ordre ?? 0,
                    Nom = p.Nom
                });
            }

            // 2. Conseguir las ofertas (Lista normal)
            // Usamos .ToList() para traerlo todo a la memoria y trabajar tranquilo
            var ofertas = await _context.Ofertes
                .Include(o => o.PortOrigen)
                .Include(o => o.PortDesti)
                .Include(o => o.EstatOferta)
                .Where(o => o.Active == 1 && o.EstatOfertaId == estadoId)
                .ToListAsync();

            // 3. Filtrar manualmente (si es cliente)
            var listaFinal = new List<Oferte>();
            foreach (var o in ofertas)
            {
                if (clienteId == null || o.ClientId == clienteId)
                {
                    listaFinal.Add(o);
                }
            }

            // 4. Crear la lista final de resultados (El "bucle de mapeo")
            var resultado = new List<ComandaResumenDTO>();
            foreach (var o in listaFinal)
            {
                var dto = new ComandaResumenDTO();
                dto.Id = o.Id;
                dto.NumPedido = (o.NumPedido != null) ? o.NumPedido.ToString() : "N/A";
                dto.NombreOferta = o.NombreOferta;

                // Comprobar manualmente cada objeto relacionado
                dto.PuertoOrigen = (o.PortOrigen != null) ? o.PortOrigen.Nom : "Sin Puerto";
                dto.PuertoDestino = (o.PortDesti != null) ? o.PortDesti.Nom : "Sin Puerto";
                dto.Estado = (o.EstatOferta != null) ? o.EstatOferta.Estat : "Desconocido";
                dto.FechaEntrega = (o.FechaEntrega != null) ? o.FechaEntrega.Value.ToString("dd/MM/yyyy") : "Pendiente";
                dto.TrackingActualId = o.TrackingActualId;
                dto.PasosSeguimiento = listaPasosDTO;

                resultado.Add(dto);
            }

            return Ok(resultado);
        }

        //cliente: aceptar o rechazar oferta (comprobar mensaje de motivo rechazo) 
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
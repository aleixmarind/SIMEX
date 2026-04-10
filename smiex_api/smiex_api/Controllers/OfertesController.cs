using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using smiex_api.Models;

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

        // GET: api/Ofertes
        [HttpGet]
        public async Task<ActionResult<IEnumerable<Oferte>>> GetOfertes()
        {
            return await _context.Ofertes.ToListAsync();
        }

        // GET: api/Ofertes/5
        [HttpGet("{id}")]
        public async Task<ActionResult<Oferte>> GetOferte(int id)
        {
            var oferte = await _context.Ofertes.FindAsync(id);

            if (oferte == null)
            {
                return NotFound();
            }

            return oferte;
        }

        // PUT: api/Ofertes/5
        // To protect from overposting attacks, see https://go.microsoft.com/fwlink/?linkid=2123754
        [HttpPut("{id}")]
        public async Task<IActionResult> PutOferte(int id, Oferte oferte)
        {
            if (id != oferte.Id)
            {
                return BadRequest();
            }

            _context.Entry(oferte).State = EntityState.Modified;

            try
            {
                await _context.SaveChangesAsync();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!OferteExists(id))
                {
                    return NotFound();
                }
                else
                {
                    throw;
                }
            }

            return NoContent();
        }

        // POST: api/Ofertes
        // To protect from overposting attacks, see https://go.microsoft.com/fwlink/?linkid=2123754
        [HttpPost]
        public async Task<ActionResult<Oferte>> PostOferte(Oferte oferte)
        {
            _context.Ofertes.Add(oferte);
            await _context.SaveChangesAsync();

            return CreatedAtAction("GetOferte", new { id = oferte.Id }, oferte);
        }

        // DELETE: api/Ofertes/5
        [HttpDelete("{id}")]
        public async Task<IActionResult> DeleteOferte(int id)
        {
            var oferte = await _context.Ofertes.FindAsync(id);
            if (oferte == null)
            {
                return NotFound();
            }

            _context.Ofertes.Remove(oferte);
            await _context.SaveChangesAsync();

            return NoContent();
        }

        private bool OferteExists(int id)
        {
            return _context.Ofertes.Any(e => e.Id == id);
        }

        // GET: api/Ofertes/Cliente/1004
        [HttpGet("Cliente/{clienteId}")]
        public async Task<ActionResult<IEnumerable<ComandaResumenDTO>>> GetComandasPorCliente(int clienteId)
        {
            // 1. Obtenemos todos los pasos posibles para el tracking (del 1 al 9)
            var todosLosPasos = await _context.TrackingSteps
                .Where(s => s.Id <= 9) // Solo tomamos la primera tanda de pasos
                .OrderBy(s => s.Ordre)
                .Select(s => new TrackingStepDTO
                {
                    Id = s.Id,
                    Ordre = s.Ordre ?? 0,
                    Nom = s.Nom
                }).ToListAsync();

            // 2. Obtenemos las ofertas del cliente
            var comandas = await _context.Ofertes
                .Include(o => o.PortOrigen)
                .Include(o => o.PortDesti)
                .Include(o => o.EstatOferta)
                .Where(o => o.ClientId == clienteId && o.Active == 1)
                .Select(o => new ComandaResumenDTO
                {
                    Id = o.Id,
                    NumPedido = o.NumPedido.ToString(),
                    NombreOferta = o.NombreOferta,
                    PuertoOrigen = o.PortOrigen != null ? o.PortOrigen.Nom : "Sin Puerto",
                    PuertoDestino = o.PortDesti != null ? o.PortDesti.Nom : "Sin Puerto",
                    Estado = o.EstatOferta.Estat,
                    FechaEntrega = o.FechaEntrega.HasValue ? o.FechaEntrega.Value.ToString("dd/MM/yyyy") : "Pendiente",

                    // --- AQUÍ ESTÁ LA MAGIA ---
                    TrackingActualId = o.TrackingActualId, // El ID de la tabla ofertes
                    PasosSeguimiento = todosLosPasos    // Metemos la lista de los 9 pasos
                })
                .ToListAsync();

            if (comandas == null || !comandas.Any())
            {
                return NotFound();
            }

            return Ok(comandas);
        }

    }
}

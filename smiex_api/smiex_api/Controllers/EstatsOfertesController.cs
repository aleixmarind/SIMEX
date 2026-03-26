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
    public class EstatsOfertesController : ControllerBase
    {
        private readonly Simex05Context _context;

        public EstatsOfertesController(Simex05Context context)
        {
            _context = context;
        }

        // GET: api/EstatsOfertes
        [HttpGet]
        public async Task<ActionResult<IEnumerable<EstatsOferte>>> GetEstatsOfertes()
        {
            return await _context.EstatsOfertes.ToListAsync();
        }

        // GET: api/EstatsOfertes/5
        [HttpGet("{id}")]
        public async Task<ActionResult<EstatsOferte>> GetEstatsOferte(int id)
        {
            var estatsOferte = await _context.EstatsOfertes.FindAsync(id);

            if (estatsOferte == null)
            {
                return NotFound();
            }

            return estatsOferte;
        }

        // PUT: api/EstatsOfertes/5
        // To protect from overposting attacks, see https://go.microsoft.com/fwlink/?linkid=2123754
        [HttpPut("{id}")]
        public async Task<IActionResult> PutEstatsOferte(int id, EstatsOferte estatsOferte)
        {
            if (id != estatsOferte.Id)
            {
                return BadRequest();
            }

            _context.Entry(estatsOferte).State = EntityState.Modified;

            try
            {
                await _context.SaveChangesAsync();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!EstatsOferteExists(id))
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

        // POST: api/EstatsOfertes
        // To protect from overposting attacks, see https://go.microsoft.com/fwlink/?linkid=2123754
        [HttpPost]
        public async Task<ActionResult<EstatsOferte>> PostEstatsOferte(EstatsOferte estatsOferte)
        {
            _context.EstatsOfertes.Add(estatsOferte);
            await _context.SaveChangesAsync();

            return CreatedAtAction("GetEstatsOferte", new { id = estatsOferte.Id }, estatsOferte);
        }

        // DELETE: api/EstatsOfertes/5
        [HttpDelete("{id}")]
        public async Task<IActionResult> DeleteEstatsOferte(int id)
        {
            var estatsOferte = await _context.EstatsOfertes.FindAsync(id);
            if (estatsOferte == null)
            {
                return NotFound();
            }

            _context.EstatsOfertes.Remove(estatsOferte);
            await _context.SaveChangesAsync();

            return NoContent();
        }

        private bool EstatsOferteExists(int id)
        {
            return _context.EstatsOfertes.Any(e => e.Id == id);
        }
    }
}

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
    public class CiutatsController : ControllerBase
    {
        private readonly Simex05Context _context;

        public CiutatsController(Simex05Context context)
        {
            _context = context;
        }

        // GET: api/Ciutats
        [HttpGet]
        public async Task<ActionResult<IEnumerable<Ciutat>>> GetCiutats()
        {
            return await _context.Ciutats.ToListAsync();
        }

        // GET: api/Ciutats/5
        [HttpGet("{id}")]
        public async Task<ActionResult<Ciutat>> GetCiutat(int id)
        {
            var ciutat = await _context.Ciutats.FindAsync(id);

            if (ciutat == null)
            {
                return NotFound();
            }

            return ciutat;
        }

        // PUT: api/Ciutats/5
        // To protect from overposting attacks, see https://go.microsoft.com/fwlink/?linkid=2123754
        [HttpPut("{id}")]
        public async Task<IActionResult> PutCiutat(int id, Ciutat ciutat)
        {
            if (id != ciutat.Id)
            {
                return BadRequest();
            }

            _context.Entry(ciutat).State = EntityState.Modified;

            try
            {
                await _context.SaveChangesAsync();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!CiutatExists(id))
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

        // POST: api/Ciutats
        // To protect from overposting attacks, see https://go.microsoft.com/fwlink/?linkid=2123754
        [HttpPost]
        public async Task<ActionResult<Ciutat>> PostCiutat(Ciutat ciutat)
        {
            _context.Ciutats.Add(ciutat);
            await _context.SaveChangesAsync();

            return CreatedAtAction("GetCiutat", new { id = ciutat.Id }, ciutat);
        }

        // DELETE: api/Ciutats/5
        [HttpDelete("{id}")]
        public async Task<IActionResult> DeleteCiutat(int id)
        {
            var ciutat = await _context.Ciutats.FindAsync(id);
            if (ciutat == null)
            {
                return NotFound();
            }

            _context.Ciutats.Remove(ciutat);
            await _context.SaveChangesAsync();

            return NoContent();
        }

        private bool CiutatExists(int id)
        {
            return _context.Ciutats.Any(e => e.Id == id);
        }
    }
}

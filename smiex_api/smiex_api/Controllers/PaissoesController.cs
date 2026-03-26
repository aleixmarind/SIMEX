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
    [Route("api/Paissos")]
    [ApiController]
    public class PaissoesController : ControllerBase
    {
        private readonly Simex05Context _context;

        public PaissoesController(Simex05Context context)
        {
            _context = context;
        }

        // GET: api/Paissos
        [HttpGet]
        public async Task<ActionResult<IEnumerable<Paisso>>> GetPaissos()
        {
            return await _context.Paissos.ToListAsync();
        }

        // GET: api/Paissoes/5
        [HttpGet("{id}")]
        public async Task<ActionResult<Paisso>> GetPaisso(int id)
        {
            var paisso = await _context.Paissos.FindAsync(id);

            if (paisso == null)
            {
                return NotFound();
            }

            return paisso;
        }

        // PUT: api/Paissoes/5
        // To protect from overposting attacks, see https://go.microsoft.com/fwlink/?linkid=2123754
        [HttpPut("{id}")]
        public async Task<IActionResult> PutPaisso(int id, Paisso paisso)
        {
            if (id != paisso.Id)
            {
                return BadRequest();
            }

            _context.Entry(paisso).State = EntityState.Modified;

            try
            {
                await _context.SaveChangesAsync();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!PaissoExists(id))
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

        // POST: api/Paissoes
        // To protect from overposting attacks, see https://go.microsoft.com/fwlink/?linkid=2123754
        [HttpPost]
        public async Task<ActionResult<Paisso>> PostPaisso(Paisso paisso)
        {
            _context.Paissos.Add(paisso);
            await _context.SaveChangesAsync();

            return CreatedAtAction("GetPaisso", new { id = paisso.Id }, paisso);
        }

        // DELETE: api/Paissoes/5
        [HttpDelete("{id}")]
        public async Task<IActionResult> DeletePaisso(int id)
        {
            var paisso = await _context.Paissos.FindAsync(id);
            if (paisso == null)
            {
                return NotFound();
            }

            _context.Paissos.Remove(paisso);
            await _context.SaveChangesAsync();

            return NoContent();
        }

        private bool PaissoExists(int id)
        {
            return _context.Paissos.Any(e => e.Id == id);
        }
    }
}

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
    public class UsuarisController : ControllerBase
    {
        private readonly Simex05Context _context;

        public UsuarisController(Simex05Context context)
        {
            _context = context;
        }

        // GET: api/Usuaris
        [HttpGet]
        public async Task<ActionResult<IEnumerable<Usuari>>> GetUsuaris()
        {
            return await _context.Usuaris.ToListAsync();
        }

        // GET: api/Usuaris/5
        [HttpGet("{id}")]
        public async Task<ActionResult<Usuari>> GetUsuari(int id)
        {
            var usuari = await _context.Usuaris.FindAsync(id);

            if (usuari == null)
            {
                return NotFound();
            }

            return usuari;
        }

        // PUT: api/Usuaris/5
        // To protect from overposting attacks, see https://go.microsoft.com/fwlink/?linkid=2123754
        [HttpPut("{id}")]
        public async Task<IActionResult> PutUsuari(int id, Usuari usuari)
        {
            if (id != usuari.Id)
            {
                return BadRequest();
            }

            _context.Entry(usuari).State = EntityState.Modified;

            try
            {
                await _context.SaveChangesAsync();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!UsuariExists(id))
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

        // POST: api/Usuaris
        // To protect from overposting attacks, see https://go.microsoft.com/fwlink/?linkid=2123754
        [HttpPost]
        public async Task<ActionResult<Usuari>> PostUsuari(Usuari usuari)
        {
            _context.Usuaris.Add(usuari);
            await _context.SaveChangesAsync();

            return CreatedAtAction("GetUsuari", new { id = usuari.Id }, usuari);
        }

        // DELETE: api/Usuaris/5
        [HttpDelete("{id}")]
        public async Task<IActionResult> DeleteUsuari(int id)
        {
            var usuari = await _context.Usuaris.FindAsync(id);
            if (usuari == null)
            {
                return NotFound();
            }

            _context.Usuaris.Remove(usuari);
            await _context.SaveChangesAsync();

            return NoContent();
        }

        private bool UsuariExists(int id)
        {
            return _context.Usuaris.Any(e => e.Id == id);
        }
    }
}

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
    public class DocumentacionsController : ControllerBase
    {
        private readonly Simex05Context _context;

        public DocumentacionsController(Simex05Context context)
        {
            _context = context;
        }

        // GET: api/Documentacions
        [HttpGet]
        public async Task<ActionResult<IEnumerable<Documentacion>>> GetDocumentacions()
        {
            return await _context.Documentacions.ToListAsync();
        }

        // GET: api/Documentacions/5
        [HttpGet("{id}")]
        public async Task<ActionResult<Documentacion>> GetDocumentacion(int id)
        {
            var documentacion = await _context.Documentacions.FindAsync(id);

            if (documentacion == null)
            {
                return NotFound();
            }

            return documentacion;
        }

        // PUT: api/Documentacions/5
        // To protect from overposting attacks, see https://go.microsoft.com/fwlink/?linkid=2123754
        [HttpPut("{id}")]
        public async Task<IActionResult> PutDocumentacion(int id, Documentacion documentacion)
        {
            if (id != documentacion.IdDocumento)
            {
                return BadRequest();
            }

            _context.Entry(documentacion).State = EntityState.Modified;

            try
            {
                await _context.SaveChangesAsync();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!DocumentacionExists(id))
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

        // POST: api/Documentacions
        // To protect from overposting attacks, see https://go.microsoft.com/fwlink/?linkid=2123754
        [HttpPost]
        public async Task<ActionResult<Documentacion>> PostDocumentacion(Documentacion documentacion)
        {
            _context.Documentacions.Add(documentacion);
            await _context.SaveChangesAsync();

            return CreatedAtAction("GetDocumentacion", new { id = documentacion.IdDocumento }, documentacion);
        }

        // DELETE: api/Documentacions/5
        [HttpDelete("{id}")]
        public async Task<IActionResult> DeleteDocumentacion(int id)
        {
            var documentacion = await _context.Documentacions.FindAsync(id);
            if (documentacion == null)
            {
                return NotFound();
            }

            _context.Documentacions.Remove(documentacion);
            await _context.SaveChangesAsync();

            return NoContent();
        }

        private bool DocumentacionExists(int id)
        {
            return _context.Documentacions.Any(e => e.IdDocumento == id);
        }
    }
}

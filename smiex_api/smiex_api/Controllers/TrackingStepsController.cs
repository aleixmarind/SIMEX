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
    public class TrackingStepsController : ControllerBase
    {
        private readonly Simex05Context _context;

        public TrackingStepsController(Simex05Context context)
        {
            _context = context;
        }

        // GET: api/TrackingSteps
        [HttpGet]
        public async Task<ActionResult<IEnumerable<TrackingStep>>> GetTrackingSteps()
        {
            return await _context.TrackingSteps.ToListAsync();
        }

        // GET: api/TrackingSteps/5
        [HttpGet("{id}")]
        public async Task<ActionResult<TrackingStep>> GetTrackingStep(int id)
        {
            var trackingStep = await _context.TrackingSteps.FindAsync(id);

            if (trackingStep == null)
            {
                return NotFound();
            }

            return trackingStep;
        }

        // PUT: api/TrackingSteps/5
        // To protect from overposting attacks, see https://go.microsoft.com/fwlink/?linkid=2123754
        [HttpPut("{id}")]
        public async Task<IActionResult> PutTrackingStep(int id, TrackingStep trackingStep)
        {
            if (id != trackingStep.Id)
            {
                return BadRequest();
            }

            _context.Entry(trackingStep).State = EntityState.Modified;

            try
            {
                await _context.SaveChangesAsync();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!TrackingStepExists(id))
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

        // POST: api/TrackingSteps
        // To protect from overposting attacks, see https://go.microsoft.com/fwlink/?linkid=2123754
        [HttpPost]
        public async Task<ActionResult<TrackingStep>> PostTrackingStep(TrackingStep trackingStep)
        {
            _context.TrackingSteps.Add(trackingStep);
            await _context.SaveChangesAsync();

            return CreatedAtAction("GetTrackingStep", new { id = trackingStep.Id }, trackingStep);
        }

        // DELETE: api/TrackingSteps/5
        [HttpDelete("{id}")]
        public async Task<IActionResult> DeleteTrackingStep(int id)
        {
            var trackingStep = await _context.TrackingSteps.FindAsync(id);
            if (trackingStep == null)
            {
                return NotFound();
            }

            _context.TrackingSteps.Remove(trackingStep);
            await _context.SaveChangesAsync();

            return NoContent();
        }

        private bool TrackingStepExists(int id)
        {
            return _context.TrackingSteps.Any(e => e.Id == id);
        }
    }
}

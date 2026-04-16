using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using smiex_api.Models;
using simex_api.DTOs;

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

        // GET: api/Usuaris/5
        [HttpGet("{id}")]
        public async Task<ActionResult> GetUsuari(int id)
        {
            // buscar user
            var usuari = await _context.Usuaris
                .AsNoTracking()
                .FirstOrDefaultAsync(u => u.Id == id); 

            if (usuari == null) return NotFound();

            return Ok(new
            {
                id = usuari.Id,
                nom = usuari.Nom,
                cognoms = usuari.Cognoms,
                correu = usuari.Correu,
                rolId = usuari.RolId,
                active = usuari.Active
            });
        }

        [HttpPost("login")]
        public async Task<ActionResult<LoginResponse>> Login([FromBody] Dictionary<string, string> datos)
        {
            if (!datos.TryGetValue("usuari", out var userText) ||
                !datos.TryGetValue("contrasenya", out var password))
            {
                return BadRequest(new { message = "Datos incompletos" });
            }
                                                                                                                                                 
            var usuari = await _context.Usuaris
                .Include(u => u.Rol)
                .FirstOrDefaultAsync(u => u.Correu == userText && u.Contrasenya == password);

      
            if (usuari == null)
            {
                return Unauthorized(new { message = "Usuario o contraseña incorrectos" });
            }

            
            var response = new LoginResponse
            {
                Id = usuari.Id,
                Nombre = usuari.Nom,
                Email = usuari.Correu,
                RolId = usuari.RolId,
                Tipo = (usuari.RolId == 1004) ? "Cliente" : "Agente",
                NombreRolReal = usuari.Rol?.Rol1
            };

            return Ok(response);
        }
    }
}

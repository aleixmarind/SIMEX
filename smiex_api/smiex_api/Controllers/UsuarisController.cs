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
                active = usuari.Active,
                DniFotoFrontal = usuari.DniFotoFrontal != null ? Convert.ToBase64String(usuari.DniFotoFrontal!) : null,
                DniFotoTrasera = usuari.DniFotoTrasera != null ? Convert.ToBase64String(usuari.DniFotoTrasera!) : null
            });
        }
        [HttpPost("{id}/dni-frontal")]
        public async Task<IActionResult> UpdateDniFrontal(int id, [FromBody] string base64)
        {
            var user = await _context.Usuaris.FindAsync(id);
            if (user == null) return NotFound();

            // Limpiamos la cadena si viene con el prefijo "data:image/jpeg;base64,"
            var pureBase64 = base64.Contains(",") ? base64.Split(',')[1] : base64;

            // Convertimos de String Base64 a array de bytes (binario)
            user.DniFotoFrontal = Convert.FromBase64String(pureBase64);

            await _context.SaveChangesAsync();
            return Ok();
        }

        [HttpPost("{id}/dni-trasero")]
        public async Task<IActionResult> UpdateDniTrasero(int id, [FromBody] string base64)
        {
            var user = await _context.Usuaris.FindAsync(id);
            if (user == null) return NotFound();

            var pureBase64 = base64.Contains(",") ? base64.Split(',')[1] : base64;
            user.DniFotoTrasera = Convert.FromBase64String(pureBase64);

            await _context.SaveChangesAsync();
            return Ok();
        }

        [HttpPost("login")]
        public async Task<ActionResult<LoginResponse>> Login([FromBody] Dictionary<string, string> datos)
        {
            if (!datos.TryGetValue("usuari", out var userText) ||
                !datos.TryGetValue("contrasenya", out var password))
            {
                return BadRequest(new { message = "Datos incompletos" });
            }

            var usuari = await _context.Usuaris.Include(u => u.Rol)
                .FirstOrDefaultAsync(u => u.Correu == userText && u.Contrasenya == password);

            if (usuari == null) return Unauthorized(new { message = "Credenciales incorrectas" });

            string? frontalBase64 = (usuari.DniFotoFrontal != null && usuari.DniFotoFrontal.Length>0)
                ? Convert.ToBase64String(usuari.DniFotoFrontal) : null;
            string? traseraBase64 = (usuari.DniFotoTrasera != null && usuari.DniFotoTrasera.Length>0)
                ? Convert.ToBase64String(usuari.DniFotoTrasera) : null;

            var response = new LoginResponse {
                Id = usuari.Id,
                Nombre = usuari.Nom,
                Email = usuari.Correu,
                RolId = usuari.RolId,
                Tipo = (usuari.RolId == 1004) ? "Cliente" : "Agente",
                NombreRolReal = usuari.Rol?.Rol1,
                DniFotoFrontal = frontalBase64,
                DniFotoTrasera = traseraBase64
            };

            return Ok(response);
        }
    }
}

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
        // GET: api/Usuaris/5
        [HttpGet("{id}")]
        public async Task<ActionResult> GetUsuari(int id)
        {
            // 1. Buscamos el usuario SIN incluir relaciones pesadas
            var usuari = await _context.Usuaris
                .AsNoTracking() // Mejora el rendimiento
                .FirstOrDefaultAsync(u => u.Id == id);

            if (usuari == null) return NotFound();

            // 2. Devolvemos un objeto anónimo plano (Esto NUNCA da error 500)
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
            // 1. Extraemos los valores del diccionario
            if (!datos.TryGetValue("usuari", out var userText) ||
                !datos.TryGetValue("contrasenya", out var password))
            {
                return BadRequest(new { message = "Datos incompletos" });
            }

            // 2. Buscamos en la base de datos INCLUYENDO la tabla Rol
            var usuari = await _context.Usuaris
                .Include(u => u.Rol) // Esto es vital para que NombreRolReal no sea nulo
                .FirstOrDefaultAsync(u => u.Correu == userText && u.Contrasenya == password);

            // 3. Si no existe, error
            if (usuari == null)
            {
                return Unauthorized(new { message = "Usuario o contraseña incorrectos" });
            }

            // 4. Creamos la respuesta usando el DTO (Aquí ya no te dará error)
            var response = new LoginResponse
            {
                Id = usuari.Id,
                Nombre = usuari.Nom,
                Email = usuari.Correu,
                RolId = usuari.RolId,
                // 1004 = Cliente en la base de datos, el resto son Agentes
                Tipo = (usuari.RolId == 1004) ? "Cliente" : "Agente",
                NombreRolReal = usuari.Rol?.Rol1
            };

            return Ok(response);
        }
    }
}

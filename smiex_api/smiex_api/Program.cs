using Microsoft.EntityFrameworkCore;
using smiex_api.Models;

var builder = WebApplication.CreateBuilder(args);

// 1. Añadir el contexto de la base de datos
builder.Services.AddDbContext<Simex05Context>(options =>
    options.UseSqlServer(builder.Configuration.GetConnectionString("DefaultConnection")));

builder.Services.AddControllers();

// 2. CONFIGURAR SWAGGER (Añade estas dos líneas)
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

var app = builder.Build();

// 3. HABILITAR LA INTERFAZ VISUAL (Modifica esta parte)
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI(); // Esto es lo que crea la página en /swagger
}

app.UseAuthorization();
app.MapControllers();
app.Run();
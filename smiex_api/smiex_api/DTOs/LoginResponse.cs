namespace simex_api.DTOs // Ajusta 'simex_api' al nombre de tu proyecto
{
    public class LoginResponse
    {
        public int Id { get; set; }
        public string Nombre { get; set; } = null!;
        public string Email { get; set; } = null!;
        public int RolId { get; set; }

        // Propiedades extra para facilitar la lógica al móvil
        public string Tipo { get; set; } = null!; // Aquí enviaremos "Agente" o "Cliente"
        public string? NombreRolReal { get; set; } // El texto exacto de la tabla 'rols'
    }
}
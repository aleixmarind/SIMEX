namespace simex_api.DTOs // Ajusta 'simex_api' al nombre de tu proyecto
{
    public class LoginResponse
    {
        public int Id { get; set; }
        public string Nombre { get; set; } = null!;
        public string Email { get; set; } = null!;
        public int RolId { get; set; }
        public string Tipo { get; set; } = null!;
        public string? NombreRolReal { get; set; }
        // cambiar byte[] por string? /revisar/
        public string? DniFotoFrontal { get; set; }
        public string? DniFotoTrasera { get; set; }
    }
}
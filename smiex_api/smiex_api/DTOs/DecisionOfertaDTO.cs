namespace smiex_api.DTOs
{
    // Para recibir la decisión del cliente
    public class DecisionOfertaDTO
    {
        public bool Aceptada { get; set; }
        public string? MotivoRechazo { get; set; } // El campo que pediste para el motivo
    }
}
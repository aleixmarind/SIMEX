namespace smiex_api.DTOs
{
    //para recibir la decisión del cliente
    public class DecisionOfertaDTO
    {
        public bool Aceptada { get; set; }
        public string? MotivoRechazo { get; set; } 
    }
}
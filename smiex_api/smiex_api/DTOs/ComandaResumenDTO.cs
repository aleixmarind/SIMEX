


public class ComandaResumenDTO
{
    public int Id { get; set; }
    public string? NumPedido { get; set; }
    public string? NombreOferta { get; set; }
    public string? PuertoOrigen { get; set; }
    public string? PuertoDestino { get; set; }
    public string? Estado { get; set; }
    public string? FechaEntrega { get; set; }

    // --- Añade esto aquí ---
    public int? TrackingActualId { get; set; }
    public List<TrackingStepDTO>? PasosSeguimiento { get; set; }
}

public class OfertaDetalleDTO
{
    public int Id { get; set; }
    public string PuertoOrigen { get; set; }
    public string PuertoDestino { get; set; }
    public int TrackingActualId { get; set; } // El ID que sacamos de la DB
    public List<TrackingStepDTO> PasosSeguimiento { get; set; } // Lista de los 9 pasos
}

public class TrackingStepDTO
{
    public int Id { get; set; }
    public int Ordre { get; set; }
    public string Nom { get; set; }
}
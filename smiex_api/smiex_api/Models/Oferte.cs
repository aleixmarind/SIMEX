using System;
using System.Collections.Generic;

namespace smiex_api.Models;

public partial class Oferte
{
    public int Id { get; set; }

    public int TipusTransportId { get; set; }

    public int TipusFluxeId { get; set; }

    public int TipusCarregaId { get; set; }

    public int IncotermId { get; set; }

    public int ClientId { get; set; }

    public string? Comentaris { get; set; }

    public int? AgentComercialId { get; set; }

    public int? TransportistaId { get; set; }

    public decimal? PesBrut { get; set; }

    public decimal? Volum { get; set; }

    public int TipusValidacioId { get; set; }

    public int? PortOrigenId { get; set; }

    public int? PortDestiId { get; set; }

    public int? AeroportOrigenId { get; set; }

    public int? AeroportDestiId { get; set; }

    public int? LiniaTransportMaritimId { get; set; }

    public int EstatOfertaId { get; set; }

    public int OperadorId { get; set; }

    public DateOnly DataCreacio { get; set; }

    public DateOnly? DataValidessaInicial { get; set; }

    public DateOnly? DataValidessaFina { get; set; }

    public string? RaoRebuig { get; set; }

    public int? TipusContenidorId { get; set; }

    public string? NombreOferta { get; set; }

    public DateOnly? FechaEnvio { get; set; }

    public DateOnly? FechaEntrega { get; set; }

    public int? NumPedido { get; set; }

    public int? IdFreight { get; set; }

    public int Active { get; set; }

    public virtual Aeroport? AeroportDesti { get; set; }

    public virtual Aeroport? AeroportOrigen { get; set; }

    public virtual ICollection<Documentacion> Documentacions { get; set; } = new List<Documentacion>();

    public virtual EstatsOferte EstatOferta { get; set; } = null!;

    public virtual Freight? IdFreightNavigation { get; set; }

    public virtual Incoterm Incoterm { get; set; } = null!;

    public virtual LiniesTransportMaritim? LiniaTransportMaritim { get; set; }

    public virtual Usuari Operador { get; set; } = null!;

    public virtual Port? PortDesti { get; set; }

    public virtual Port? PortOrigen { get; set; }

    public virtual TipusCarrega TipusCarrega { get; set; } = null!;

    public virtual TipusContenidor? TipusContenidor { get; set; }

    public virtual TipusFlux TipusFluxe { get; set; } = null!;

    public virtual TipusTransport TipusTransport { get; set; } = null!;

    public virtual TipusValidacion TipusValidacio { get; set; } = null!;

    public virtual Transportiste? Transportista { get; set; }
}

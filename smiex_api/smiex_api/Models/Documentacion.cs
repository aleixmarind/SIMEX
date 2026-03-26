using System;
using System.Collections.Generic;

namespace smiex_api.Models;

public partial class Documentacion
{
    public int IdDocumento { get; set; }

    public string NombreDoc { get; set; } = null!;

    public string UrlArchivo { get; set; } = null!;

    public int IdOferta { get; set; }

    public virtual Oferte IdOfertaNavigation { get; set; } = null!;
}

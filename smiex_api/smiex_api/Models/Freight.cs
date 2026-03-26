using System;
using System.Collections.Generic;

namespace smiex_api.Models;

public partial class Freight
{
    public int IdFreight { get; set; }

    public string? Tipus { get; set; }

    public virtual ICollection<Oferte> Ofertes { get; set; } = new List<Oferte>();
}

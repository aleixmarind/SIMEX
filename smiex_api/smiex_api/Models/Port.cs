using System;
using System.Collections.Generic;

namespace smiex_api.Models;

public partial class Port
{
    public int Id { get; set; }

    public string Nom { get; set; } = null!;

    public int CiutatId { get; set; }

    public virtual Ciutat Ciutat { get; set; } = null!;

    public virtual ICollection<Oferte> OfertePortDestis { get; set; } = new List<Oferte>();

    public virtual ICollection<Oferte> OfertePortOrigens { get; set; } = new List<Oferte>();
}

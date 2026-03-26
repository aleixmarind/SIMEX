using System;
using System.Collections.Generic;

namespace smiex_api.Models;

public partial class Paisso
{
    public int Id { get; set; }

    public string? Nom { get; set; }

    public virtual ICollection<Ciutat> Ciutats { get; set; } = new List<Ciutat>();
}

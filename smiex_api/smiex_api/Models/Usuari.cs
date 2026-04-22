using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations.Schema;

namespace smiex_api.Models;

public partial class Usuari
{
    public int Id { get; set; }

    public string Correu { get; set; } = null!;

    public string Contrasenya { get; set; } = null!;

    public string Nom { get; set; } = null!;

    public string Cognoms { get; set; } = null!;

    public int RolId { get; set; }

    public int Active { get; set; }

    public virtual ICollection<Notificacione> NotificacioneIdUserrecieveNavigations { get; set; } = new List<Notificacione>();

    public virtual ICollection<Notificacione> NotificacioneIdUsersendNavigations { get; set; } = new List<Notificacione>();

    public virtual ICollection<Oferte> Ofertes { get; set; } = new List<Oferte>();

    public virtual Rol Rol { get; set; } = null!;

    [Column("dni_foto_frontal")]
    public byte[]? DniFotoFrontal { get; set; }

    [Column("dni_foto_trasera")]
    public byte[]? DniFotoTrasera { get; set; }
}

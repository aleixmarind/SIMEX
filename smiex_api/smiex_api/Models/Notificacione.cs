using System;
using System.Collections.Generic;

namespace smiex_api.Models;

public partial class Notificacione
{
    public int IdNotificacion { get; set; }

    public string? Titulo { get; set; }

    public string? Mensaje { get; set; }

    public DateOnly? Fecha { get; set; }

    public int? IdUsersend { get; set; }

    public int? IdUserrecieve { get; set; }

    public virtual Usuari? IdUserrecieveNavigation { get; set; }

    public virtual Usuari? IdUsersendNavigation { get; set; }
}

using System;
using System.Collections.Generic;
using Microsoft.EntityFrameworkCore;

namespace smiex_api.Models;

public partial class Simex05Context : DbContext
{
    public Simex05Context()
    {
    }

    public Simex05Context(DbContextOptions<Simex05Context> options)
        : base(options)
    {
    }

    public virtual DbSet<Aeroport> Aeroports { get; set; }

    public virtual DbSet<Ciutat> Ciutats { get; set; }

    public virtual DbSet<Client> Clients { get; set; }

    public virtual DbSet<Documentacion> Documentacions { get; set; }

    public virtual DbSet<EstatsOferte> EstatsOfertes { get; set; }

    public virtual DbSet<Freight> Freights { get; set; }

    public virtual DbSet<Incoterm> Incoterms { get; set; }

    public virtual DbSet<LiniesTransportMaritim> LiniesTransportMaritims { get; set; }

    public virtual DbSet<Notificacione> Notificaciones { get; set; }

    public virtual DbSet<Oferte> Ofertes { get; set; }

    public virtual DbSet<Paisso> Paissos { get; set; }

    public virtual DbSet<Port> Ports { get; set; }

    public virtual DbSet<Rol> Rols { get; set; }

    public virtual DbSet<TipusCarrega> TipusCarregas { get; set; }

    public virtual DbSet<TipusContenidor> TipusContenidors { get; set; }

    public virtual DbSet<TipusFlux> TipusFluxes { get; set; }

    public virtual DbSet<TipusIncoterm> TipusIncoterms { get; set; }

    public virtual DbSet<TipusTransport> TipusTransports { get; set; }

    public virtual DbSet<TipusValidacion> TipusValidacions { get; set; }

    public virtual DbSet<TrackingStep> TrackingSteps { get; set; }

    public virtual DbSet<Transportiste> Transportistes { get; set; }

    public virtual DbSet<Usuari> Usuaris { get; set; }

    protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
#warning To protect potentially sensitive information in your connection string, you should move it out of source code. You can avoid scaffolding the connection string by using the Name= syntax to read it from configuration - see https://go.microsoft.com/fwlink/?linkid=2131148. For more guidance on storing connection strings, see https://go.microsoft.com/fwlink/?LinkId=723263.
        => optionsBuilder.UseSqlServer("Server=51.83.192.177;Database=simex05;User Id=simex05;Password=FLUXO05simex09;Trusted_Connection=False;TrustServerCertificate=True;");

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        modelBuilder.Entity<Aeroport>(entity =>
        {
            entity.ToTable("aeroports");

            entity.Property(e => e.Id).HasColumnName("id");
            entity.Property(e => e.CiutatId).HasColumnName("ciutat_id");
            entity.Property(e => e.Codi)
                .HasMaxLength(5)
                .IsUnicode(false)
                .IsFixedLength()
                .HasColumnName("codi");
            entity.Property(e => e.Nom)
                .HasMaxLength(255)
                .IsUnicode(false)
                .HasColumnName("nom");

            entity.HasOne(d => d.Ciutat).WithMany(p => p.Aeroports)
                .HasForeignKey(d => d.CiutatId)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("FK_aeroports_ciutats");
        });

        modelBuilder.Entity<Ciutat>(entity =>
        {
            entity.ToTable("ciutats");

            entity.Property(e => e.Id).HasColumnName("id");
            entity.Property(e => e.Nom)
                .HasMaxLength(100)
                .IsUnicode(false)
                .HasColumnName("nom");
            entity.Property(e => e.PaisId).HasColumnName("pais_id");

            entity.HasOne(d => d.Pais).WithMany(p => p.Ciutats)
                .HasForeignKey(d => d.PaisId)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("FK_ciutats_paissos");
        });

        modelBuilder.Entity<Client>(entity =>
        {
            entity.ToTable("clients");

            entity.Property(e => e.Id).HasColumnName("id");
        });

        modelBuilder.Entity<Documentacion>(entity =>
        {
            entity.HasKey(e => e.IdDocumento).HasName("PK__document__5D2EE7E55F252A01");

            entity.ToTable("documentacion");

            entity.Property(e => e.IdDocumento).HasColumnName("id_documento");
            entity.Property(e => e.IdOferta).HasColumnName("id_oferta");
            entity.Property(e => e.NombreDoc)
                .HasMaxLength(255)
                .IsUnicode(false)
                .HasColumnName("nombreDoc");
            entity.Property(e => e.UrlArchivo)
                .HasMaxLength(255)
                .IsUnicode(false)
                .HasColumnName("urlArchivo");

            entity.HasOne(d => d.IdOfertaNavigation).WithMany(p => p.Documentacions)
                .HasForeignKey(d => d.IdOferta)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("fk_documento_oferta");
        });

        modelBuilder.Entity<EstatsOferte>(entity =>
        {
            entity.ToTable("estats_ofertes");

            entity.Property(e => e.Id).HasColumnName("id");
            entity.Property(e => e.Estat)
                .HasMaxLength(50)
                .IsUnicode(false)
                .HasColumnName("estat");
        });

        modelBuilder.Entity<Freight>(entity =>
        {
            entity.HasKey(e => e.IdFreight).HasName("PK__freight__9FF309294E599357");

            entity.ToTable("freight");

            entity.Property(e => e.IdFreight).HasColumnName("id_freight");
            entity.Property(e => e.Tipus)
                .HasMaxLength(255)
                .IsUnicode(false)
                .HasColumnName("tipus");
        });

        modelBuilder.Entity<Incoterm>(entity =>
        {
            entity.ToTable("incoterms");

            entity.Property(e => e.Id).HasColumnName("id");
            entity.Property(e => e.TipusIncontermId).HasColumnName("tipus_inconterm_id");
            entity.Property(e => e.TrackingStepsId).HasColumnName("tracking_steps_id");

            entity.HasOne(d => d.TipusInconterm).WithMany(p => p.Incoterms)
                .HasForeignKey(d => d.TipusIncontermId)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("FK_incoterms_tipus_incoterms");

            entity.HasOne(d => d.TrackingSteps).WithMany(p => p.Incoterms)
                .HasForeignKey(d => d.TrackingStepsId)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("FK_incoterms_tracking_steps");
        });

        modelBuilder.Entity<LiniesTransportMaritim>(entity =>
        {
            entity.ToTable("linies_transport_maritim");

            entity.Property(e => e.Id).HasColumnName("id");
            entity.Property(e => e.CiutatId).HasColumnName("ciutat_id");
            entity.Property(e => e.Nom)
                .HasMaxLength(50)
                .IsUnicode(false)
                .HasColumnName("nom");

            entity.HasOne(d => d.Ciutat).WithMany(p => p.LiniesTransportMaritims)
                .HasForeignKey(d => d.CiutatId)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("FK_linies_transport_maritim_ciutats");
        });

        modelBuilder.Entity<Notificacione>(entity =>
        {
            entity.HasKey(e => e.IdNotificacion).HasName("PK__notifica__8270F9A528CEC347");

            entity.ToTable("notificaciones");

            entity.Property(e => e.IdNotificacion)
                .ValueGeneratedNever()
                .HasColumnName("id_notificacion");
            entity.Property(e => e.Fecha).HasColumnName("fecha");
            entity.Property(e => e.IdUserrecieve).HasColumnName("id_userrecieve");
            entity.Property(e => e.IdUsersend).HasColumnName("id_usersend");
            entity.Property(e => e.Mensaje)
                .HasMaxLength(255)
                .IsUnicode(false)
                .HasColumnName("mensaje");
            entity.Property(e => e.Titulo)
                .HasMaxLength(255)
                .IsUnicode(false)
                .HasColumnName("titulo");

            entity.HasOne(d => d.IdUserrecieveNavigation).WithMany(p => p.NotificacioneIdUserrecieveNavigations)
                .HasForeignKey(d => d.IdUserrecieve)
                .HasConstraintName("fk_receptor");

            entity.HasOne(d => d.IdUsersendNavigation).WithMany(p => p.NotificacioneIdUsersendNavigations)
                .HasForeignKey(d => d.IdUsersend)
                .HasConstraintName("fk_emisor");
        });

        modelBuilder.Entity<Oferte>(entity =>
        {
            entity.ToTable("ofertes");

            entity.Property(e => e.Id).HasColumnName("id");
            entity.Property(e => e.Active).HasColumnName("active");
            entity.Property(e => e.AeroportDestiId).HasColumnName("aeroport_desti_id");
            entity.Property(e => e.AeroportOrigenId).HasColumnName("aeroport_origen_id");
            entity.Property(e => e.AgentComercialId).HasColumnName("agent_comercial_id");
            entity.Property(e => e.ClientId).HasColumnName("client_id");
            entity.Property(e => e.Comentaris)
                .HasMaxLength(255)
                .IsUnicode(false)
                .HasColumnName("comentaris");
            entity.Property(e => e.DataCreacio).HasColumnName("data_creacio");
            entity.Property(e => e.DataValidessaFina).HasColumnName("data_validessa_fina");
            entity.Property(e => e.DataValidessaInicial).HasColumnName("data_validessa_inicial");
            entity.Property(e => e.EstatOfertaId).HasColumnName("estat_oferta_id");
            entity.Property(e => e.FechaEntrega).HasColumnName("fecha_entrega");
            entity.Property(e => e.FechaEnvio).HasColumnName("fecha_envio");
            entity.Property(e => e.IdFreight).HasColumnName("id_freight");
            entity.Property(e => e.IncotermId).HasColumnName("incoterm_id");
            entity.Property(e => e.LiniaTransportMaritimId).HasColumnName("linia_transport_maritim_id");
            entity.Property(e => e.NombreOferta)
                .HasMaxLength(255)
                .IsUnicode(false)
                .HasColumnName("nombre_oferta");
            entity.Property(e => e.NumPedido).HasColumnName("num_pedido");
            entity.Property(e => e.OperadorId).HasColumnName("operador_id");
            entity.Property(e => e.PesBrut)
                .HasColumnType("numeric(18, 2)")
                .HasColumnName("pes_brut");
            entity.Property(e => e.PortDestiId).HasColumnName("port_desti_id");
            entity.Property(e => e.PortOrigenId).HasColumnName("port_origen_id");
            entity.Property(e => e.RaoRebuig)
                .HasMaxLength(50)
                .IsUnicode(false)
                .HasColumnName("rao_rebuig");
            entity.Property(e => e.TipusCarregaId).HasColumnName("tipus_carrega_id");
            entity.Property(e => e.TipusContenidorId).HasColumnName("tipus_contenidor_id");
            entity.Property(e => e.TipusFluxeId).HasColumnName("tipus_fluxe_id");
            entity.Property(e => e.TipusTransportId).HasColumnName("tipus_transport_id");
            entity.Property(e => e.TipusValidacioId).HasColumnName("tipus_validacio_id");
            entity.Property(e => e.TransportistaId).HasColumnName("transportista_id");
            entity.Property(e => e.Volum)
                .HasColumnType("numeric(18, 2)")
                .HasColumnName("volum");

            entity.HasOne(d => d.AeroportDesti).WithMany(p => p.OferteAeroportDestis)
                .HasForeignKey(d => d.AeroportDestiId)
                .HasConstraintName("FK_ofertes_aeroports1");

            entity.HasOne(d => d.AeroportOrigen).WithMany(p => p.OferteAeroportOrigens)
                .HasForeignKey(d => d.AeroportOrigenId)
                .HasConstraintName("FK_ofertes_aeroports");

            entity.HasOne(d => d.EstatOferta).WithMany(p => p.Ofertes)
                .HasForeignKey(d => d.EstatOfertaId)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("FK_ofertes_estats_ofertes");

            entity.HasOne(d => d.IdFreightNavigation).WithMany(p => p.Ofertes)
                .HasForeignKey(d => d.IdFreight)
                .HasConstraintName("fk_freight_ofertes");

            entity.HasOne(d => d.Incoterm).WithMany(p => p.Ofertes)
                .HasForeignKey(d => d.IncotermId)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("FK_ofertes_incoterms");

            entity.HasOne(d => d.LiniaTransportMaritim).WithMany(p => p.Ofertes)
                .HasForeignKey(d => d.LiniaTransportMaritimId)
                .HasConstraintName("FK_ofertes_linies_transport_maritim");

            entity.HasOne(d => d.Operador).WithMany(p => p.Ofertes)
                .HasForeignKey(d => d.OperadorId)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("FK_ofertes_usuaris");

            entity.HasOne(d => d.PortDesti).WithMany(p => p.OfertePortDestis)
                .HasForeignKey(d => d.PortDestiId)
                .HasConstraintName("FK_ofertes_ports1");

            entity.HasOne(d => d.PortOrigen).WithMany(p => p.OfertePortOrigens)
                .HasForeignKey(d => d.PortOrigenId)
                .HasConstraintName("FK_ofertes_ports");

            entity.HasOne(d => d.TipusCarrega).WithMany(p => p.Ofertes)
                .HasForeignKey(d => d.TipusCarregaId)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("FK_ofertes_tipus_carrega");

            entity.HasOne(d => d.TipusContenidor).WithMany(p => p.Ofertes)
                .HasForeignKey(d => d.TipusContenidorId)
                .HasConstraintName("FK_ofertes_tipus_contenidors");

            entity.HasOne(d => d.TipusFluxe).WithMany(p => p.Ofertes)
                .HasForeignKey(d => d.TipusFluxeId)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("FK_ofertes_tipus_fluxes");

            entity.HasOne(d => d.TipusTransport).WithMany(p => p.Ofertes)
                .HasForeignKey(d => d.TipusTransportId)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("FK_ofertes_tipus_transports");

            entity.HasOne(d => d.TipusValidacio).WithMany(p => p.Ofertes)
                .HasForeignKey(d => d.TipusValidacioId)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("FK_ofertes_tipus_validacions");

            entity.HasOne(d => d.Transportista).WithMany(p => p.Ofertes)
                .HasForeignKey(d => d.TransportistaId)
                .HasConstraintName("FK_ofertes_transportistes");
        });

        modelBuilder.Entity<Paisso>(entity =>
        {
            entity.ToTable("paissos");

            entity.Property(e => e.Id).HasColumnName("id");
            entity.Property(e => e.Nom)
                .HasMaxLength(100)
                .IsUnicode(false)
                .HasColumnName("nom");
        });

        modelBuilder.Entity<Port>(entity =>
        {
            entity.ToTable("ports");

            entity.Property(e => e.Id).HasColumnName("id");
            entity.Property(e => e.CiutatId).HasColumnName("ciutat_id");
            entity.Property(e => e.Nom)
                .HasMaxLength(50)
                .IsUnicode(false)
                .HasColumnName("nom");

            entity.HasOne(d => d.Ciutat).WithMany(p => p.Ports)
                .HasForeignKey(d => d.CiutatId)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("FK_ports_ciutats");
        });

        modelBuilder.Entity<Rol>(entity =>
        {
            entity.ToTable("rols");

            entity.Property(e => e.Id).HasColumnName("id");
            entity.Property(e => e.Rol1)
                .HasMaxLength(50)
                .IsUnicode(false)
                .HasColumnName("rol");
        });

        modelBuilder.Entity<TipusCarrega>(entity =>
        {
            entity.ToTable("tipus_carrega");

            entity.Property(e => e.Id).HasColumnName("id");
            entity.Property(e => e.Tipus)
                .HasMaxLength(50)
                .IsUnicode(false)
                .HasColumnName("tipus");
        });

        modelBuilder.Entity<TipusContenidor>(entity =>
        {
            entity.ToTable("tipus_contenidors");

            entity.Property(e => e.Id).HasColumnName("id");
            entity.Property(e => e.Tipus)
                .HasMaxLength(50)
                .IsUnicode(false)
                .HasColumnName("tipus");
        });

        modelBuilder.Entity<TipusFlux>(entity =>
        {
            entity.ToTable("tipus_fluxes");

            entity.Property(e => e.Id).HasColumnName("id");
            entity.Property(e => e.Tipus)
                .HasMaxLength(50)
                .IsUnicode(false)
                .HasColumnName("tipus");
        });

        modelBuilder.Entity<TipusIncoterm>(entity =>
        {
            entity.ToTable("tipus_incoterms");

            entity.Property(e => e.Id).HasColumnName("id");
            entity.Property(e => e.Codi)
                .HasMaxLength(5)
                .IsUnicode(false)
                .IsFixedLength()
                .HasColumnName("codi");
            entity.Property(e => e.Nom)
                .HasMaxLength(50)
                .IsUnicode(false)
                .HasColumnName("nom");
        });

        modelBuilder.Entity<TipusTransport>(entity =>
        {
            entity.ToTable("tipus_transports");

            entity.Property(e => e.Id).HasColumnName("id");
            entity.Property(e => e.Tipus)
                .HasMaxLength(50)
                .IsUnicode(false)
                .HasColumnName("tipus");
        });

        modelBuilder.Entity<TipusValidacion>(entity =>
        {
            entity.ToTable("tipus_validacions");

            entity.Property(e => e.Id).HasColumnName("id");
            entity.Property(e => e.Tipus)
                .HasMaxLength(50)
                .IsUnicode(false)
                .HasColumnName("tipus");
        });

        modelBuilder.Entity<TrackingStep>(entity =>
        {
            entity.ToTable("tracking_steps");

            entity.Property(e => e.Id).HasColumnName("id");
            entity.Property(e => e.Nom)
                .HasMaxLength(50)
                .IsUnicode(false)
                .HasColumnName("nom");
            entity.Property(e => e.Ordre).HasColumnName("ordre");
        });

        modelBuilder.Entity<Transportiste>(entity =>
        {
            entity.ToTable("transportistes");

            entity.Property(e => e.Id).HasColumnName("id");
            entity.Property(e => e.CiutatId).HasColumnName("ciutat_id");
            entity.Property(e => e.Nom)
                .HasMaxLength(50)
                .IsUnicode(false)
                .HasColumnName("nom");

            entity.HasOne(d => d.Ciutat).WithMany(p => p.Transportistes)
                .HasForeignKey(d => d.CiutatId)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("FK_transportistes_ciutats");
        });

        modelBuilder.Entity<Usuari>(entity =>
        {
            entity.ToTable("usuaris");

            entity.Property(e => e.Id).HasColumnName("id");
            entity.Property(e => e.Active).HasColumnName("active");
            entity.Property(e => e.Cognoms)
                .HasMaxLength(50)
                .IsUnicode(false)
                .HasColumnName("cognoms");
            entity.Property(e => e.Contrasenya)
                .HasMaxLength(256)
                .IsUnicode(false)
                .HasColumnName("contrasenya");
            entity.Property(e => e.Correu)
                .HasMaxLength(50)
                .IsUnicode(false)
                .HasColumnName("correu");
            entity.Property(e => e.Nom)
                .HasMaxLength(50)
                .IsUnicode(false)
                .HasColumnName("nom");
            entity.Property(e => e.RolId).HasColumnName("rol_id");

            entity.HasOne(d => d.Rol).WithMany(p => p.Usuaris)
                .HasForeignKey(d => d.RolId)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("FK_usuaris_rols");
        });

        OnModelCreatingPartial(modelBuilder);
    }

    partial void OnModelCreatingPartial(ModelBuilder modelBuilder);
}

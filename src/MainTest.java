import static org.junit.Assert.*;

import modele.CarteTempete;
import modele.Zone;
import org.junit.Test;

import modele.Plateau;

public class MainTest {
    @Test
    public void PlateauTest() {
        Plateau p = new Plateau();
    }

    @Test
    public void testIntersectionOeilTempete() {
        Plateau p = new Plateau();
        int oeilI = p.getOeilI(); // 2
        int oeilJ = p.getOeilJ(); // 2
        Zone.TypeZone type = Zone.TypeZone.INDICE_BOITE;
        Zone indiceI = Zone.creerZone(oeilI, 0, type, 'i', p);

        Zone indiceJ = Zone.creerZone(0, oeilJ, type, 'j', p);

        p.placerZonePourTest(oeilI, 0, indiceI);
        p.placerZonePourTest(0, oeilJ, indiceJ);

        indiceI.reveler();
        indiceJ.reveler();

        assertNull("L'intersection est toujours l'oeil", p.getZone(oeilI, oeilJ));
        assertTrue("L'objet est en attente", p.testGetObjetsEnAttente().contains(type));
        p.leVentSouffle(CarteTempete.Direction.SUD, 1); // On déplace l'oeil

        assertSame("L'objet est bien sur la Zone (2,2)", type, p.getZone(2, 2).get_objet_pose());
        assertTrue("L'attente est vide", p.testGetObjetsEnAttente().isEmpty());

    }

    // Test Spécifiques

    // Test Spécifique à l'Alpiniste
    @Test
    public void testDeplacementAlpiniste() {
        Plateau p = new Plateau();
        modele.Joueur j = modele.Joueur.creerJoueur(modele.Joueur.Role.ALPINISTE, "", p);
        modele.Joueur j2 = modele.Joueur.creerJoueur(modele.Joueur.Role.METEOROLOGUE, "", p);
        Zone base = p.getZone(0, 0);
        j.setZone(base);
        j2.setZone(base);
        Zone voisin = p.getZone(0, 1);
        voisin.viderSable();
        assertTrue("Zone sans sable accessible pour les deux joueurs", j.getZonesAccessibles(p).contains(voisin));
        voisin.ajouterSable(1);
        assertTrue("Zone avec 1 sable accessible pour les deux joueurs", j.getZonesAccessibles(p).contains(voisin));
        voisin.ajouterSable(1);
        assertTrue("Zone avec >= 2 sable  accessible pour l'alpiniste", j.getZonesAccessibles(p).contains(voisin));
        assertFalse("Zone avec >= 2 sable  non accessible pour le météorologue", j2.getZonesAccessibles(p).contains(voisin));
        j.setZone(voisin);
        assertTrue("Zone avec >= 2 sable  accessible lorsque l'alpiniste est dessus", j2.getZonesAccessibles(p).contains(voisin));
        j2.setZone(voisin);
        assertFalse("Le joueur peut sortir d'une case ensablée si l'alpiniste est dessus", j2.getZonesAccessibles(p).isEmpty());
        j.setZone(base);
        assertTrue("Le joueur ne peut pas sortir d'une case ensablée si l'alpiniste est dessus", j2.getZonesAccessibles(p).isEmpty());

    }
}

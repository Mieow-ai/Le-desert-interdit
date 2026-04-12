import static org.junit.Assert.*;

import modele.CarteTempete;
import modele.Joueur;
import modele.Zone;
import org.junit.Test;

import modele.Plateau;

public class MainTest {
    @Test
    public void PlateauTest() {
        Plateau p = new Plateau();
        modele.Joueur j = modele.Joueur.creerJoueur(modele.Joueur.Role.METEOROLOGUE, "", p);
        Zone oeil = p.getZone(2, 2);
        j.setZone(p.getZone(2, 1));
        assertThrows("Creuser oeil", IllegalStateException.class, () -> j.creuserZone(oeil));
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

    // Test Spécifiques aux rôles

    // Test Spécifique à l'Alpiniste
    @Test
    public void testDeplacementAlpiniste() {
        Plateau p = new Plateau();
        modele.Joueur alpiniste = modele.Joueur.creerJoueur(modele.Joueur.Role.ALPINISTE, "", p);
        modele.Joueur j2 = modele.Joueur.creerJoueur(modele.Joueur.Role.METEOROLOGUE, "", p);
        Zone base = p.getZone(0, 0);
        alpiniste.setZone(base);
        j2.setZone(base);
        Zone voisin = p.getZone(0, 1);
        voisin.viderSable();
        assertTrue("Zone sans sable accessible pour les deux joueurs",
                alpiniste.getZonesAccessibles(p).contains(voisin) && j2.getZonesAccessibles(p).contains(voisin));
        voisin.ajouterSable(1);
        assertTrue("Zone avec 1 sable accessible pour les deux joueurs",
                alpiniste.getZonesAccessibles(p).contains(voisin));
        voisin.ajouterSable(1);
        assertTrue("Zone avec >= 2 sable  accessible pour l'alpiniste",
                alpiniste.getZonesAccessibles(p).contains(voisin));
        assertFalse("Zone avec >= 2 sable  non accessible pour le météorologue",
                j2.getZonesAccessibles(p).contains(voisin));
        alpiniste.setZone(voisin);
        assertTrue("Zone avec >= 2 sable  accessible lorsque l'alpiniste est dessus",
                j2.getZonesAccessibles(p).contains(voisin));
        j2.setZone(voisin);
        assertFalse("Le joueur peut sortir d'une case ensablée si l'alpiniste est dessus",
                j2.getZonesAccessibles(p).isEmpty());
        alpiniste.setZone(base);
        assertTrue("Le joueur ne peut pas sortir d'une case ensablée si l'alpiniste est dessus",
                j2.getZonesAccessibles(p).isEmpty());
    }

    // Test Spécifique à l'Explorateur
    @Test
    public void testDeplacementExplorateur() {
        Plateau p = new Plateau();
        modele.Joueur explo = modele.Joueur.creerJoueur(Joueur.Role.EXPLORATEUR, "", p);
        modele.Joueur j2 = modele.Joueur.creerJoueur(modele.Joueur.Role.METEOROLOGUE, "", p);
        Zone base = p.getZone(0, 0);
        explo.setZone(base);
        j2.setZone(base);
        Zone voisin = p.getZone(1, 1);
        voisin.viderSable();
        assertTrue("Zone en diagonale accessible pour l'explorateur", explo.getZonesAccessibles(p).contains(voisin));
        assertFalse("Zone en diagonale non accessible pour le lambda", j2.getZonesAccessibles(p).contains(voisin));

        voisin.ajouterSable(2);
        assertFalse("Zone en diagonale ensablée non accessible pour l'explorateur",
                explo.getZonesAccessibles(p).contains(voisin));
        modele.Joueur alpiniste = modele.Joueur.creerJoueur(modele.Joueur.Role.ALPINISTE, "", p);
        alpiniste.setZone(voisin);
        assertTrue("Zone en diagonale ensablée accessible pour l'explorateur si l'alpiniste est dessus",
                explo.getZonesAccessibles(p).contains(voisin));
    }

    @Test
    public void testCreusableExplorateur() {
        Plateau p = new Plateau();
        modele.Joueur explo = modele.Joueur.creerJoueur(Joueur.Role.EXPLORATEUR, "", p);
        modele.Joueur j2 = modele.Joueur.creerJoueur(modele.Joueur.Role.METEOROLOGUE, "", p);
        Zone base = p.getZone(0, 0);
        explo.setZone(base);
        j2.setZone(base);
        Zone voisin = p.getZone(1, 1);
        voisin.viderSable();
        assertTrue("Zone en diagonale sans sable  creusable pour l'explorateur",
                explo.getZonesCreusables(p).contains(voisin));
        voisin.reveler();
        assertFalse("Zone en diagonale sans sable et révélée non creusable pour l'explorateur",
                explo.getZonesCreusables(p).contains(voisin));
        voisin.ajouterSable(2);
        assertFalse("Zone en diagonale non creusable pour le lambda", j2.getZonesCreusables(p).contains(voisin));
        assertTrue("Zone en diagonale avec sable creusable pour l'explorateur",
                explo.getZonesCreusables(p).contains(voisin));
    }

    @Test
    public void testCreuserArcheologue() {
        Plateau p = new Plateau();
        modele.Joueur archeo = modele.Joueur.creerJoueur(Joueur.Role.ARCHEOLOGUE, "", p);
        modele.Joueur j2 = modele.Joueur.creerJoueur(modele.Joueur.Role.METEOROLOGUE, "", p);
        Zone base = p.getZone(0, 0);
        archeo.setZone(base);
        j2.setZone(base);
        Zone voisin = p.getZone(1, 0);
        voisin.viderSable();
        voisin.ajouterSable(2);
        archeo.creuserZone(voisin);
        assertEquals("L'archéologue a enlever 2 sables", 0, voisin.getNbSable());
        voisin.ajouterSable(2);
        j2.creuserZone(voisin);
        assertEquals("Le lambda a enlevé 1 sable", 1, voisin.getNbSable());
        archeo.creuserZone(voisin);
        assertEquals("L'archéologue a enlever 1 sable car il en restait qu'un", 0, voisin.getNbSable());
    }

    @Test
    public void testAmortirMeteorologue() {
        Plateau p = new Plateau();
        modele.Joueur meteoro = modele.Joueur.creerJoueur(modele.Joueur.Role.METEOROLOGUE, "", p);
        modele.Joueur lambda = modele.Joueur.creerJoueur(Joueur.Role.ALPINISTE, "", p);
        meteoro.amortirTempete();
        assertEquals("L'archéologue amorti la tempête", 1, meteoro.getAndResetReductionTempete());
        assertEquals("Le get amorti reset bie,", 0, meteoro.getAndResetReductionTempete());
        assertThrows("Autre amorti", IllegalStateException.class, () -> lambda.amortirTempete());
    }

    // Les déplacements de la navigatrice nécessitent le controleur et les boutons
    // spéciaux
    // Une fois que le joueur est choisi le déplacement dépend entièrement du joueur
    // par délégation donc
    // Les joueurs peuvent être déplacés dans les bonnes cases (ensablées et
    // accompagné pour l'alpiniste,
    // en diagonale pour l'explorateur etc

    @Test
    public void testPorteuseEau() {
        Plateau p = new Plateau();
        modele.Joueur porteuse = modele.Joueur.creerJoueur(Joueur.Role.PORTEUSE_EAU, "", p);
        modele.Joueur lambda = modele.Joueur.creerJoueur(Joueur.Role.ALPINISTE, "", p);
        assertEquals("nv Eau 5 pour la porteuse", 5, porteuse.getNvEau());
        assertEquals("nv Eau 4 pour le lambda", 4, lambda.getNvEau());
        porteuse.setZone(p.getZone(0, 0));
        lambda.setZone(p.getZone(0, 0));
        lambda.boire_eau(1);
        porteuse.donner_eau(lambda);
        assertEquals("Donner de l'eau ne coute pas d'action à la porteuse", 4, porteuse.getNb_action_restante());
        assertEquals("Lambda a bien gagné un niveau d'eau", 4, lambda.getNvEau());
        lambda.boire_eau(1);
        porteuse.setZone(p.getZone(1, 0));
        porteuse.donner_eau(lambda);
        assertEquals("Lambda a bien gagné un niveau d'eau (case adjacente)", 4, lambda.getNvEau());

        lambda.boire_eau(2);
        porteuse.boire_eau(2);
        Zone eau = Zone.creerZone(0, 0, Zone.TypeZone.EAU, ' ', p);
        porteuse.setZone(eau);
        lambda.setZone(eau);
        porteuse.creuserZone(eau);
        assertEquals("Lambda a bien gagné deux niveaux d'eau ", 4, lambda.getNvEau());
        assertEquals("Porteuse a bien gagné deux niveaux d'eau ", 4, lambda.getNvEau());
        lambda.boire_eau(2);
        porteuse.boire_eau(2);
        porteuse.creuserZone(eau);
        assertEquals("Lambda ne doit pas gagner deux niveaux d'eau ", 2, lambda.getNvEau());
        assertEquals("Porteuse a bien gagné deux niveaux d'eau ", 4, lambda.getNvEau());

    }

}

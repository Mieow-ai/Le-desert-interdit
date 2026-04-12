package modele;

/**
 * Classe représentant le paquet de cartes équipements
 */
public class PaquetEquipement extends Paquet<CarteEquipement> {
    @Override
    protected void creerPaquet() {
        // 2 cartes par type
        for (CarteEquipement.Type type : CarteEquipement.Type.values()) {
            this.pioche.add(new CarteEquipement(type));
        }
    }
}



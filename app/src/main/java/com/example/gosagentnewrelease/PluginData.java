package com.example.gosagentnewrelease;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PluginData {
    public List<Card> Cards;
    public int MarkerType = 0;
    public List<MarkerData> Markers = new ArrayList<>();
    public List<LotData> LotData = new ArrayList<>();
    public String[] CoordinatesLot;

    public static final String MAPKIT_API_KEY = "51b30e9b-2f6e-4d68-93d9-a8ae19941ef1";

    public static final String LAST_CAMERA_POSITION = "lastCameraPosition";

    public static final String DB_HOST = "http://cj62918.tmweb.ru/chat.php";
    public static final String TABLE_NAME = "tablestype";
    public static final String ACTION_GET_MARKERS = "?action=select&table=";
    public static final String ACTION_GET_LOT_INFORMATIONS = "?action=getData&table=";
    public static final List<String> AllTegLot = Arrays.asList(
            "organizers", "number", "fossil", "name", "description",
            "location", "work", "square", "object", "transmission_methods",
            "initial_price", "term", "services", "monthly_payment",
            "link"
    );

    public PluginData() {
        initializeCards();
    }

    public String getIDCameraPosition() {
        return LAST_CAMERA_POSITION + MarkerType;
    }

    private void initializeCards(){
        Cards = new ArrayList<>();

        Cards.add(new Card(R.drawable.ic_museum_form_icon, "Аренда, безвозмездное пользование и иные договоры в отношении государственного и муниципального имущества", 1));
        Cards.add(new Card(R.drawable.ic_city_hall_from_icon, "Аренда и продажа земельных участков, заключение договора о комплексном развитии территории", 2));
        Cards.add(new Card(R.drawable.ic_hardware_store_from_icon, "Строительство", 3));
        Cards.add(new Card(R.drawable.ic_bakery_from_icon, "Охотхозяйственные соглашения", 4));
        Cards.add(new Card(R.drawable.ic_finance_from_icon, "Пользование участками недр", 5));
        Cards.add(new Card(R.drawable.ic_train_station_from_icon, "Государственно частное партнерство (соглашения о ГЧП/МЧП, концессионные соглашения)", 6));
        Cards.add(new Card(R.drawable.ic_postal_code_prefix_from_icon, "Аренда лесных участков и продажа лесных насаждений", 7));
        Cards.add(new Card(R.drawable.ic_political_from_icon, "Продажа государственного (муниципального) имущества и имущества госкомпаний", 8));
        Cards.add(new Card(R.drawable.ic_electronics_store_from_icon, "Передача прав на единые технологии", 9));
        Cards.add(new Card(R.drawable.ic_aquarium_from_icon, "Водопользование", 10));
        Cards.add(new Card(R.drawable.ic_fishing_pier_from_icon, "Рыболовство и добыча водных биоресурсов", 11));
        Cards.add(new Card(R.drawable.ic_bank_from_icon, "ЖКХ", 12));
        Cards.add(new Card(R.drawable.ic_lawyer_from_icon, "Реализация имущества должников", 13));
        Cards.add(new Card(R.drawable.ic_general_contractor_from_icon, "Создание искусственных земельных участков", 14));
        Cards.add(new Card(R.drawable.ic_volume_control_telephone_from_icon, "Размещение рекламных конструкций", 15));
        Cards.add(new Card(R.drawable.ic_electrician_from_icon, "Продажа объектов электроэнергетики", 16));
        Cards.add(new Card(R.drawable.ic_electronics_store_from_icon, "Лицензии на оказание услуг связи", 17));
    }

    public void clear() {
        Markers.clear();
        LotData.clear();
        CoordinatesLot = null;
    }
}

class MarkerData {
    public int id;
    public double coordinatesX;
    public double coordinatesY;

    public MarkerData(int id, double coordinatesX, double coordinatesY) {
        this.id = id;
        this.coordinatesX = coordinatesX;
        this.coordinatesY = coordinatesY;
    }

    public MarkerData() { }
}

class LotData {
    public List<String> description = new ArrayList<>();
    public String number;
    public String link;
    public String location;
    public String initialPrice;
    public String monthlyPayment;

    public LotData(List<String> description) { this.description.addAll(description); }

    public LotData() { }

    public LotData(String numberLot, String linkLot, String locationLot, String initialPriceLot, String monthlyPaymentLot, List<String> lotConfigs) {
        this.number = numberLot;
        this.link = linkLot;
        this.location = locationLot;
        this.initialPrice = initialPriceLot;
        this.monthlyPayment = monthlyPaymentLot;
        this.description.addAll(lotConfigs);
    }

}
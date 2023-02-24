package com.example.gosagentnewrelease.data;

import android.util.SparseBooleanArray;

import com.example.gosagentnewrelease.R;
import com.example.gosagentnewrelease.custom.types.Card;
import com.example.gosagentnewrelease.custom.types.FavoritesLot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PluginData {
    public List<Card> Cards;
    public int MarkerType = 0;
    public List<com.example.gosagentnewrelease.custom.types.LotData> LotData = new ArrayList<>();
    public HashSet<FavoritesLot> FavoritesLots = new HashSet<>();
    public Double CoordinatesLotLat;
    public Double CoordinatesLotLang;
    public Boolean IsFavorites = false;

    public static final String MAPKIT_API_KEY = "51b30e9b-2f6e-4d68-93d9-a8ae19941ef1";

    public static final String LAST_CAMERA_POSITION = "lastCameraPosition";

    public static final String DB_HOST = "http://cj62918.tmweb.ru/chat.php";
    public static final String TABLE_NAME = "LotCards";
    public static final String ACTION_GET_MARKERS = "?action=select&table=";
    public static final String ACTION_GET_LOT_INFORMATION = "?action=getData&table=";
    public static final String ACTION_GET_FAVORITES_LOT_INFORMATION = "?action=getFavoritesData&table=";
    public static final List<String> AllTegLot = Arrays.asList(
            "number", "lotStatus", "lotType", "lotName", "lotDescription",
            "priceMin", "priceStep", "deposit", "lotImages", "lotAttachments",
            "subjectRFCode", "category", "isStopped", "estateAddress",
            "biddStartTime", "biddEndTime", "lat", "lang", "lotlink"
    );
    public static HashMap<String, Set<Integer>> Country = new HashMap();
    public static List<String> TypeLot = new ArrayList<>();

    public PluginData() {
        initializeCards();
        initializeCountry();
        initializeTypeLot();
    }

    public String getIDCameraPosition() {
        return LAST_CAMERA_POSITION + MarkerType;
    }

    public Boolean existFavoritesLots(String nameLot) {
        for (FavoritesLot favoritesLots : FavoritesLots)
            if (favoritesLots.get_name().equals(nameLot))
                return true;

        return false;
    }

    public void deleteFavoritesLot(FavoritesLot favoritesLot) {
        FavoritesLots.remove(favoritesLot);
    }

    private void initializeCards(){
        Cards = new ArrayList<>();
        // toDo changed image...
        Cards.add(new Card(R.drawable.ic_car_dealer, "Транспорт", 22));
        Cards.add(new Card(R.drawable.ic_general_contractor, "Недвижимость", 7));
        Cards.add(new Card(R.drawable.ic_lawyer_from_icon, "Земельные участки", 2));
        Cards.add(new Card(R.drawable.ic_finance, "Акции и доли", 5));
        Cards.add(new Card(R.drawable.ic_political, "Права пользования и лицензии", 6));
        Cards.add(new Card(R.drawable.ic_hardware_store, "Строительство и развите территорий", 23));
        Cards.add(new Card(R.drawable.ic_postal_code_prefix_from_icon, "Государственно-частное партнерство", 17));
        Cards.add(new Card(R.drawable.ic_bank_from_icon, "ЖКХ", 900));
        Cards.add(new Card(R.drawable.ic_museum_form_icon, "Прочие", 13));
        // toDo added BD
        // Cards.add(new Card(R.drawable.ic_car_repair, "Металические конструкции", 18));
        // Cards.add(new Card(R.drawable.ic_atm, "Без объявления цены", 19));
    }

    private void initializeCountry() {
        Country.put("Алтайский край", new HashSet<>(Arrays.asList(22)));
        Country.put("Амурская область", new HashSet<>(Arrays.asList(28)) );
        Country.put("Архангельская область", new HashSet<>(Arrays.asList(29)) );
        Country.put("Астраханская область", new HashSet<>(Arrays.asList(30)) );
        Country.put("Белгородская область", new HashSet<>(Arrays.asList(31)) );
        Country.put("Брянская область", new HashSet<>(Arrays.asList(32)));
        Country.put("Владимирская область", new HashSet<>(Arrays.asList(33)));
        Country.put("Волгоградская область", new HashSet<>(Arrays.asList(34)));
        Country.put("Вологодская область", new HashSet<>(Arrays.asList(35)));
        Country.put("Воронежская область", new HashSet<>(Arrays.asList(36)));
        Country.put("Москва", new HashSet<>(Arrays.asList(77)));
        Country.put("Санкт-Петербург", new HashSet<>(Arrays.asList(78)));
        Country.put("Севастополь", new HashSet<>(Arrays.asList(92)) );
        Country.put("Донецкая Народная Республика", new HashSet<>(Arrays.asList(93)));
        Country.put("Еврейская автономная область", new HashSet<>(Arrays.asList(79)));
        Country.put("Забайкальский край", new HashSet<>(Arrays.asList(75)));
        Country.put("Запорожская область", new HashSet<>(Arrays.asList(90)));
        Country.put("Ивановская область", new HashSet<>(Arrays.asList(37)));
        Country.put("Байконур", new HashSet<>(Arrays.asList(99)));
        Country.put("Иркутская область", new HashSet<>(Arrays.asList(38)));
        Country.put("Кабардино-Балкарская Республика", new HashSet<>(Arrays.asList(7)));
        Country.put("Калининградская область", new HashSet<>(Arrays.asList(39)));
        Country.put("Калужская область", new HashSet<>(Arrays.asList(40)));
        Country.put("Камчатский край", new HashSet<>(Arrays.asList(41)));
        Country.put("Карачаево-Черкесская Республика", new HashSet<>(Arrays.asList(9)));
        Country.put("Кемеровская область", new HashSet<>(Arrays.asList(42)));
        Country.put("Кировская область", new HashSet<>(Arrays.asList(43)));
        Country.put("Костромская область", new HashSet<>(Arrays.asList(44)));
        Country.put("Краснодарский край", new HashSet<>(Arrays.asList(23)));
        Country.put("Красноярский край", new HashSet<>(Arrays.asList(24)));
        Country.put("Курганская область", new HashSet<>(Arrays.asList(45)));
        Country.put("Курская область", new HashSet<>(Arrays.asList(46)));
        Country.put("Ленинградская область", new HashSet<>(Arrays.asList(47)));
        Country.put("Липецкая область", new HashSet<>(Arrays.asList(48)));
        Country.put("Луганская Народная Республика", new HashSet<>(Arrays.asList(94)));
        Country.put("Магаданская область", new HashSet<>(Arrays.asList(49)));
        Country.put("Московская область", new HashSet<>(Arrays.asList(50)));
        Country.put("Мурманская область", new HashSet<>(Arrays.asList(51)));
        Country.put("Ненецкий автономный округ", new HashSet<>(Arrays.asList(83)));
        Country.put("Нижегородская область", new HashSet<>(Arrays.asList(52)));
        Country.put("Новгородская область", new HashSet<>(Arrays.asList(53)));
        Country.put("Сахалинская область", new HashSet<>(Arrays.asList(65)));
        Country.put("Новосибирская область", new HashSet<>(Arrays.asList(54)));
        Country.put("Омская область", new HashSet<>(Arrays.asList(55)));
        Country.put("Оренбургская область", new HashSet<>(Arrays.asList(56)));
        Country.put("Орловская область", new HashSet<>(Arrays.asList(57)));
        Country.put("Пермский край", new HashSet<>(Arrays.asList(59)));
        Country.put("Пензенская область", new HashSet<>(Arrays.asList(58)));
        Country.put("Приморский край", new HashSet<>(Arrays.asList(25)));
        Country.put("Псковская область", new HashSet<>(Arrays.asList(60)));
    }

    private void initializeTypeLot() {
        TypeLot.add("Реализация имущества, обращенного в собственность государства");
        TypeLot.add("Продажа государственного и муниципального имущества");
        TypeLot.add("Аренда и продажа земельных участков");
        TypeLot.add("Аренда, безвозмездное пользование, доверительное управление...");// имуществом, иные договоры, предусматривающие переход прав в отношении государственного или муниципального имущества, продажа имущества ФГУП");
        TypeLot.add("Продажа права пользования участком недр");
        TypeLot.add("Реализация имущества должников");
        TypeLot.add("Продажа акций по результатам доверительного управления");
        TypeLot.add("Комплексное развитие территорий");
        TypeLot.add("ЖКХ");
        TypeLot.add("На право заключения концессионного соглашения");
        TypeLot.add("Закрепление и предоставление доли квоты добычи крабов...");// в инвестиционных целях");
        TypeLot.add("Продажа объектов незавершенного строительства");
        TypeLot.add("Закрепление долей квот добычи водных биоресурсов...");// и/или право пользования водными биоресурсами");
        TypeLot.add("Публичные торги по продаже земельных участков");
        TypeLot.add("Купля-продажа природных алмазов специальных размеров");
        TypeLot.add("Право пользования рыбоводным участком");
        TypeLot.add("Право пользования рыболовным участком");
        TypeLot.add("Право заключения предварительного договора поставки донного грунта");
        TypeLot.add("Соглашение о государственно-частном партнерстве...");//, соглашение о муниципально-частном партнерстве");
        TypeLot.add("Водопользование");
        TypeLot.add("Продажа объектов электроэнергетики");
        TypeLot.add("Лицензии на оказание услуг связи");
        TypeLot.add("Аренда лесных участков и продажа лесных насаждений");
        TypeLot.add("Охотхозяйственные соглашения");
        TypeLot.add("Размещение рекламных конструкций");
        TypeLot.add("Иной вид торгов");
    }

    public void clear() {
        LotData.clear();
        CoordinatesLotLat = null;
        CoordinatesLotLang = null;
        IsFavorites = false;
    }
}


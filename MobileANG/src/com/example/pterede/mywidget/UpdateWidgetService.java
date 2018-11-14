package com.example.pterede.mywidget;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import com.lfg.mobileang.MainActivity;
import com.lfg.mobileang.R;

import java.util.Random;

/**
 * Created by pterede on 9/15/2016.
 */
public class UpdateWidgetService extends Service {
    private static final String LOG = "de.vogella.android.widget.example";

    String[][] name = {
            {"H", "Hydrogen", "composed of the Greek elements hydro- and -gen meaning 'water-forming'"}
            , {"He", "Helium", "the Greek helios, 'sun'"}
            , {"Li", "Lithium", "the Greek lithos, 'stone'"}
            , {"Be", "Beryllium", "beryl, a mineral"}
            , {"B", "Boron", "borax, a mineral"}
            , {"C", "Carbon", "the Latin carbo, 'coal'"}
            , {"N", "Nitrogen", "the Greek nitron and '-gen' meaning 'niter-forming'"}
            , {"O", "Oxygen", "from the Greek oxy-, both 'sharp' and 'acid', and -gen, meaning 'acid-forming'"}
            , {"F", "Fluorine", "the Latin fluere, 'to flow'"}
            , {"Ne", "Neon", "the Greek neos, meaning 'new'"}
            , {"Na", "Sodium", "the English word soda (natrium in Latin)[3]"}
            , {"Mg", "Magnesium", "Magnesia, a district of Eastern Thessaly in Greece"}
            , {"Al", "Aluminium", "from alumina, a compound (originally aluminum)"}
            , {"Si", "Silicon", "from the Latin silex, 'flint' (originally silicium)"}
            , {"P", "Phosphorus", "the Greek phoosphoros, 'carrying light'"}
            , {"S", "Sulfur", "the Latin sulphur, 'fire and brimstone'[5]"}
            , {"Cl", "Chlorine", "the Greek chloros, 'greenish yellow'"}
            , {"Ar", "Argon", "the Greek argos, 'idle'"}
            , {"K", "Potassium", "New Latin potassa, 'potash' (kalium in Latin)[3]"}
            , {"Ca", "Calcium", "the Latin calx, 'lime'"}
            , {"Sc", "Scandium", "Scandia, the Latin name for Scandinavia"}
            , {"Ti", "Titanium", "Titans, the sons of the Earth goddess of Greek mythology"}
            , {"V", "Vanadium", "Vanadis, an Old Norse name for the Scandinavian goddess Freyja"}
            , {"Cr", "Chromium", "the Greek chroma, 'color'"}
            , {"Mn", "Manganese", "corrupted from magnesia negra, see Magnesium"}
            , {"Fe", "Iron", "English word (ferrum in Latin)"}
            , {"Co", "Cobalt", "the German word Kobold, 'goblin'"}
            , {"Ni", "Nickel", "from Swedish kopparnickel, containing the German word Nickel, 'goblin'"}
            , {"Cu", "Copper", "English word (Latin cuprum)"}
            , {"Zn", "Zinc", "the German Zink"}
            , {"Ga", "Gallium", "Gallia, the Latin name for France"}
            , {"Ge", "Germanium", "Germania, the Latin name for Germany"}
            , {"As", "Arsenic", "English word (Latin arsenicum)"}
            , {"Se", "Selenium", "the Greek selene, 'moon'"}
            , {"Br", "Bromine", "the Greek bromos, 'stench'"}
            , {"Kr", "Krypton", "the Greek kryptos, 'hidden'"}
            , {"Rb", "Rubidium", "the Latin rubidus, 'deep red'"}
            , {"Sr", "Strontium", "Strontian, a small town in Scotland"}
            , {"Y", "Yttrium", "Ytterby, Sweden"}
            , {"Zr", "Zirconium", "Persian Zargun, 'gold-colored'; German Zirkoon, 'jargoon'"}
            , {"Nb", "Niobium", "Niobe, daughter of king Tantalus from Greek mythology"}
            , {"Mo", "Molybdenum", "the Greek molybdos meaning 'lead'"}
            , {"Tc", "Technetium", "the Greek tekhnètos meaning 'artificial'"}
            , {"Ru", "Ruthenium", "Ruthenia, the New Latin name for Russia"}
            , {"Rh", "Rhodium", "the Greek rhodos, meaning 'rose coloured'"}
            , {"Pd", "Palladium", "the then recently discovered asteroid Pallas, considered a planet at the time"}
            , {"Ag", "Silver", "English word (argentum in Latin)[3]"}
            , {"Cd", "Cadmium", "the New Latin cadmia, from King Kadmos"}
            , {"In", "Indium", "indigo"}
            , {"Sn", "Tin", "English word (stannum in Latin)"}
            , {"Sb", "Antimony", "composed from the Greek anti, 'against', and monos, 'alone' (stibium in Latin)"}
            , {"Te", "Tellurium", "Latin tellus, 'earth'"}
            , {"I", "Iodine", "French iode (after the Greek ioeides, 'violet')"}
            , {"Xe", "Xenon", "the Greek xenos, 'strange'"}
            , {"Cs", "Caesium", "the Latin caesius, 'sky blue'"}
            , {"Ba", "Barium", "the Greek barys, 'heavy'"}
            , {"La", "Lanthanum", "the Greek lanthanein, 'to lie hidden'"}
            , {"Ce", "Cerium", "the then recently discovered asteroid Ceres, considered a planet at the time"}
            , {"Pr", "Praseodymium", "the Greek praseios didymos meaning 'green twin'"}
            , {"Nd", "Neodymium", "the Greek neos didymos meaning 'new twin'"}
            , {"Pm", "Promethium", "Prometheus of Greek mythology who stole fire from the Gods and gave it to humans"}
            , {"Sm", "Samarium", "Samarskite, the name of the mineral from which it was first isolated"}
            , {"Eu", "Europium", "Europe"}
            , {"Gd", "Gadolinium", "Johan Gadolin, chemist, physicist and mineralogist"}
            , {"Tb", "Terbium", "Ytterby, Sweden"}
            , {"Dy", "Dysprosium", "the Greek dysprositos, 'hard to get'"}
            , {"Ho", "Holmium", "Holmia, the New Latin name for Stockholm"}
            , {"Er", "Erbium", "Ytterby, Sweden"}
            , {"Tm", "Thulium", "Thule, the ancient name for Scandinavia"}
            , {"Yb", "Ytterbium", "Ytterby, Sweden"}
            , {"Lu", "Lutetium", "Lutetia, the Latin name for Paris"}
            , {"Hf", "Hafnium", "Hafnia, the New Latin name for Copenhagen"}
            , {"Ta", "Tantalum", "King Tantalus, father of Niobe from Greek mythology"}
            , {"W", "Tungsten", "the Swedish tung sten, 'heavy stone' (W is wolfram, the old name of the tungsten mineral wolframite)[3]"}
            , {"Re", "Rhenium", "Rhenus, the Latin name for the river Rhine"}
            , {"Os", "Osmium", "the Greek osmè, meaning 'smell'"}
            , {"Ir", "Iridium", "Iris, the Greek goddess of the rainbow"}
            , {"Pt", "Platinum", "the Spanish platina, meaning 'little silver'"}
            , {"Au", "Gold", "English word (aurum in Latin)"}
            , {"Hg", "Mercury", "the New Latin name mercurius, named after the Roman god (Hg from former name hydrargyrum, from Greek hydr-, 'water', and argyros, 'silver')"}
            , {"Tl", "Thallium", "the Greek thallos, 'green twig'"}
            , {"Pb", "Lead", "English word (plumbum in Latin)[3]"}
            , {"Bi", "Bismuth", "German word, now obsolete"}
            , {"Po", "Polonium", "Polonia, the New Latin name for Poland"}
            , {"At", "Astatine", "the Greek astatos, 'unstable'"}
            , {"Rn", "Radon", "From radium, as it was first detected as an emission from radium during radioactive decay"}
            , {"Fr", "Francium", "Francia, the New Latin name for France"}
            , {"Ra", "Radium", "the Latin radius, 'ray'"}
            , {"Ac", "Actinium", "the Greek aktis, 'ray'"}
            , {"Th", "Thorium", "Thor, the Scandinavian god of thunder"}
            , {"Pa", "Protactinium", "the Greek protos, 'first', and actinium, which is produced through the radioactive decay of protactinium"}
            , {"U", "Uranium", "Uranus, the seventh planet in the Solar System"}
            , {"Np", "Neptunium", "Neptune, the eighth planet in the Solar System"}
            , {"Pu", "Plutonium", "Pluto, a dwarf planet in the Solar System (considered the ninth planet at the time)"}
            , {"Am", "Americium", "The Americas, as the element was first synthesized on the continent, by analogy with europium"}
            , {"Cm", "Curium", "Pierre Curie, a physicist, and Marie Curie, a physicist and chemist, named after great scientists by analogy with gadolinium"}
            , {"Bk", "Berkelium", "Berkeley, California, where the element was first synthesized, by analogy with terbium"}
            , {"Cf", "Californium", "California, where the element was first synthesized"}
            , {"Es", "Einsteinium", "Albert Einstein, physicist"}
            , {"Fm", "Fermium", "Enrico Fermi, physicist"}
            , {"Md", "Mendelevium", "Dmitri Mendeleev, chemist and inventor"}
            , {"No", "Nobelium", "Alfred Nobel, chemist, engineer, innovator, and armaments manufacturer"}
            , {"Lr", "Lawrencium", "Ernest O. Lawrence, physicist"}
            , {"Rf", "Rutherfordium", "Ernest Rutherford, chemist and physicist"}
            , {"Db", "Dubnium", "Dubna, Russia"}
            , {"Sg", "Seaborgium", "Glenn T. Seaborg, scientist"}
            , {"Bh", "Bohrium", "Niels Bohr, physicist"}
            , {"Hs", "Hassium", "Hesse, Germany, where the element was first synthesized"}
            , {"Mt", "Meitnerium", "Lise Meitner, physicist"}
            , {"Ds", "Darmstadtium", "Darmstadt, Germany, where the element was first synthesized"}
            , {"Rg", "Roentgenium", "Wilhelm Conrad Röntgen, physicist"}
            , {"Cn", "Copernicium", "Nicolaus Copernicus, astronomer"}
            , {"Uut", "Ununtrium", "IUPAC systematic element name"}
            , {"Fl", "Flerovium", "Georgy Flyorov, physicist"}
            , {"Uup", "Ununpentium", "IUPAC systematic element name"}
            , {"Lv", "Livermorium", "Lawrence Livermore National Laboratory (in Livermore, California) which collaborated with JINR on its synthesis"}
            , {"Uus", "Ununseptium", "IUPAC systematic element name"}
            , {"Uuo", "Ununoctium", "IUPAC systematic element name"}
    };

    @Override
    public void onStart(Intent intent, int startId) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this
                .getApplicationContext());

        int[] allWidgetIds = intent
                .getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);

//                ComponentName thisWidget = new ComponentName(getApplicationContext(),
//                                MyWidgetProvider.class);
//                int[] allWidgetIds2 = appWidgetManager.getAppWidgetIds(thisWidget);

        for (int widgetId : allWidgetIds) {
            // create some random data
            int number = (new Random().nextInt(name.length));

            RemoteViews remoteViews = new RemoteViews(this
                    .getApplicationContext().getPackageName(),
                    R.layout.widget1);
            Log.w("WidgetExample", String.valueOf(number));
            // Set the text
            remoteViews.setTextViewText(R.id.widget1label,
                    "(" + name[number][0] + ") = " + name[number][1] + "\n" + name[number][2]);

            // Register an onClickListener 1st Button
            {
                Intent clickIntent = new Intent(this.getApplicationContext(),
                        ExampleAppWidgetProvider.class);

                clickIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,
                        allWidgetIds);

                PendingIntent pendingIntent = PendingIntent.getBroadcast(
                        getApplicationContext(), 0, clickIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                remoteViews.setOnClickPendingIntent(R.id.widget1label, pendingIntent);
            }
            //REGISTER APP OPEN 2nd Button
            {
                Intent intent2 = new Intent(getApplicationContext(), MainActivity.class);
                PendingIntent pendingIntent2 = PendingIntent.getActivity(getApplicationContext(), 0, intent2, 0);
// Get the layout for the App Widget and attach an on-click listener to the button
                remoteViews.setOnClickPendingIntent(R.id.button2, pendingIntent2);
            }
            appWidgetManager.updateAppWidget(widgetId, remoteViews);

        }
        stopSelf();

        super.onStart(intent, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

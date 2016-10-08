package examples;

import java.util.List;
import java.util.Map;
import me.legrange.mikrotik.MikrotikApiException;

/**
 * Example to show that different character sets may work some times. 
 *
 * @author gideon
 */
public class CharacterSets extends Example {
    
    private static final String JAPANESE = "事報ハヤ久送とゅ歳用ト候新放すルドう二5春園ユヲロ然納レ部悲と被状クヘ芸一ーあぽだ野健い産隊ず";
    private static final String CRYLLIC = "Лорем ипсум долор сит амет, легере елояуентиам хис ид. Елигенди нолуиссе вих ут. Нихил";
    private static final String ARABIC = "تجهيز والمانيا تم قام. وحتّى المتاخمة ما وقد. أسر أمدها تكبّد عل. فقد بسبب ترتيب استدعى أم, مما مع غرّة، لأداء. الشتاء، عسكرياً";

    public static void main(String... args) throws Exception {
        CharacterSets ex = new CharacterSets();
        ex.connect();
        ex.test();
        ex.disconnect();
    }

    private void test() throws MikrotikApiException {
        con.execute("/ip/hotspot/user/add name=userJ comment='" + JAPANESE + "'");
        con.execute("/ip/hotspot/user/add name=userC comment='" + CRYLLIC + "'");
        con.execute("/ip/hotspot/user/add name=userA comment='" + ARABIC + "'");

        for (Map<String, String> res : con.execute("/ip/hotspot/user/print return name,comment")) {
            System.out.printf("%s : %s\n", res.get("name"), res.get("comment"));
        }
    }
}

import com.github.manolo8.simplecraft.core.placeholder.*;
import com.github.manolo8.simplecraft.core.placeholder.annotation.PlaceHolderMapping;
import net.minecraft.server.v1_13_R2.ChatComponentText;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Random;

public class RepositoryTestMain {

    public static void main(String[] args) {

//        MySQL mySQL = new MySQL();
//
//        mySQL.setDataBase("simplecraft");
//        mySQL.setUsername("simplecraft");
//        mySQL.setHost("localhost");
//        mySQL.setPassword("****");
//
//        RankRepository repository = new RankRepository(database);
//        repository.init();

//        KeyDataRepository repository = new KeyDataRepository(mySQL);
//
//        repository.init();

//        System.out.println(repository.findOrCreateBooleanData("zoeiro3", true).value());

        PlaceHolderService placeHolderService = new PlaceHolderService();

        placeHolderService.register(new RepositoryTestMain());

//        HolderString holder = new HolderString("asd", new StringProvider() {
//            @Override
//            public long lastModified() {
//                return 0;
//            }
//
//            @Override
//            public String value() {
//                return "eai xqdele {online} {beep} {lol}";
//            }
//        });

//        long lastModified = 0;
//
//        while (true) {
//            UserService.tick++;
//            if(holder.lastModified() != lastModified) {
//                lastModified = holder.lastModified();
//
//                System.out.println(holder.newValue());
//            }
//        }

    }

    @PlaceHolderMapping("online")
    class OnlinePlaceHolder implements PlaceHolder {

        Random random = new Random();
        int rnd = 0;
        long last = 0;

        /**
         * @return the value holder value
         */
        @Override
        public String value() {
            return String.valueOf(rnd);
        }

        /**
         * @return the last modification time
         */
        @Override
        public long lastModified() {
            if (System.currentTimeMillis() - last > 10) {
                last = System.currentTimeMillis();
                rnd = random.nextInt(500);
            }
            return rnd;
        }
    }

    @PlaceHolderMapping("beep")
    class BeepPlaceHolder implements PlaceHolder {

        Random random = new Random();
        int rnd = 0;
        long last = 0;

        /**
         * @return the value holder value
         */
        @Override
        public String value() {
            return String.valueOf(rnd);
        }

        /**
         * @return the last modification time
         */
        @Override
        public long lastModified() {
//            if (System.currentTimeMillis() - last > 10) {
//                last = System.currentTimeMillis();
                rnd = random.nextInt(500);
//            }
            return rnd;
        }
    }

    @PlaceHolderMapping("lol")
    class LolPlaceHolder implements PlaceHolderBuilder<ChatComponentText> {

        @Override
        public PlaceHolder build(ChatComponentText target) {
            return new PlaceHolder() {
                @Override
                public String value() {
                    return target.toString();
                }

                @Override
                public long lastModified() {
                    return 0;
                }
            };
        }
    }

}
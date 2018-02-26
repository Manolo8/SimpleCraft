public class Test {

    public static void main(String[] args) {
//        Runtime runtime = Runtime.getRuntime();
//        NumberFormat format = NumberFormat.getInstance();
//
//        System.out.println(format.format(runtime.freeMemory() / 1024));
//        CacheManager<BaseEntity> cacheManager = new CacheManager<>();
//
//        Random random = new Random();
//
//        for (int i = 0; i < 32300; i++) {
//            Clan clan = new Clan();
//            clan.setName("test" + random.nextInt());
//            clan.setUuid(UUID.randomUUID());
//            cacheManager.add(clan);
//        }
//
//
//        for (int i = 0; i < 22000; i++) {
//            Economy economy = new Economy();
//            economy.setName("Testando Também" + random.nextInt());
//            economy.setUuid(UUID.randomUUID());
//            cacheManager.add(economy);
//        }
//
//        UUID clanUUID = UUID.randomUUID();
//
//        Clan clan = new Clan();
//        clan.setName("Testando");
//        clan.setUuid(clanUUID);
//
//        UUID economyUUID = UUID.randomUUID();
//
//        Economy economy = new Economy();
//        economy.setName("Testando Também");
//        economy.setUuid(economyUUID);
//
//        cacheManager.add(economy);
//        cacheManager.add(clan);
//
//        long time = System.nanoTime();
//        for (int i = 0; i < 2000; i++) {
//            cacheManager.getIfExits(economyUUID, Economy.class);
//        }
//        System.out.println(System.nanoTime() - time);
//
//
//        System.out.println(format.format(runtime.freeMemory() / 1024));
    }
}

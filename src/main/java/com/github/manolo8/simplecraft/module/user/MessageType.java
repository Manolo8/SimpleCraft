package com.github.manolo8.simplecraft.module.user;

public enum MessageType {
    SUCCESS {
        @Override
        public String format(Object message) {
            return "§e[Êxito] §a" + String.valueOf(message);
        }
    }, ERROR {
        @Override
        public String format(Object message) {
            return "§c[Erro] §a" + String.valueOf(message);
        }
    }, TITLE {
        @Override
        public String format(Object message) {
            return "§c-> §a" + String.valueOf(message);
        }
    }, INFO {
        @Override
        public String format(Object message) {
            return "§b[Info] §a" + String.valueOf(message);
        }
    };

    public abstract String format(Object message);
}

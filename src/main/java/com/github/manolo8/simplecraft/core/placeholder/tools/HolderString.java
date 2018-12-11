package com.github.manolo8.simplecraft.core.placeholder.tools;

import com.github.manolo8.simplecraft.core.placeholder.PlaceHolder;
import com.github.manolo8.simplecraft.core.placeholder.PlaceHolderService;
import com.github.manolo8.simplecraft.core.placeholder.StringProvider;

import java.util.ArrayList;
import java.util.List;

public class HolderString<P extends StringProvider> {

    public static HolderString EMPTY = new HolderString(null, new StringProvider() {
        @Override
        public long lastModified() {
            return 0;
        }

        @Override
        public String value() {
            return "EMPTY";
        }
    });

    protected final Object target;
    protected final P provider;

    protected StoredString store;

    protected String value;

    protected int lastUpdate;

    protected long lastModifiedStore;
    protected long lastModifiedProvider;

    public HolderString(Object target, P provider) {
        this.target = target;
        this.provider = provider;

        this.value = "";

        createStore(provider);
        value = store.value();

        this.lastModifiedStore = store.lastModified();
        this.lastModifiedProvider = provider.lastModified();
    }

    public P provider() {
        return provider;
    }

    protected void createStore(StringProvider provider) {

        String template = provider.value();

        List<Content> contents = new ArrayList<>();

        StringBuilder mainBuilder = new StringBuilder();
        StringBuilder keyBuilder = new StringBuilder();

        for (int i = 0; i < template.length(); i++) {
            char c = template.charAt(i);
            if (c == '{') {

                while (++i < template.length()) {
                    c = template.charAt(i);
                    if (c == '}') break;
                    keyBuilder.append(c);
                }

                String before = mainBuilder.toString();
                String key = keyBuilder.toString();

                PlaceHolder holder = PlaceHolderService.instance.get(key, target);

                if (holder == null) {
                    mainBuilder.append(before).append('{').append(key).append('}');
                } else {
                    contents.add(new Content(holder, before));
                    mainBuilder.setLength(0);
                }

                keyBuilder.setLength(0);

                continue;
            }
            mainBuilder.append(c);
        }

        if (contents.size() == 0) {
            store = new BasicStoredString(template);
        } else {
            store = new ComplexStoredString(contents.toArray(new Content[0]), mainBuilder.toString());
        }
    }

    public void checkModified() {
        long lmS = store.lastModified();
        long lmP = provider.lastModified();

        if (lmP != lastModifiedProvider || lmS != lastModifiedStore) {

            if (lmP != lastModifiedProvider) {
                lastModifiedProvider = lmP;

                createStore(provider);

                lastModifiedStore = store.lastModified();

            } else {
                lastModifiedStore = lmS;
            }

            String updated = store.value();

            if (!value.equals(updated)) value = updated;
        }
    }

    public String value() {
        return value;
    }
}

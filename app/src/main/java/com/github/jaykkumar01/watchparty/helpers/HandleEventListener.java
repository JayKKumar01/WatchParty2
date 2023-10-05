package com.github.jaykkumar01.watchparty.helpers;

import android.content.Context;
import android.widget.Toast;

import com.github.jaykkumar01.watchparty.enums.ListenerType;
import com.github.jaykkumar01.watchparty.models.EventListenerData;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class HandleEventListener {
    private static final List<EventListenerData> eventListenerList = new ArrayList<>();

    public static void add(Context context,EventListenerData eventListenerData){
        eventListenerData.setContext(context);
        eventListenerList.add(eventListenerData);
    }
    public static void removeAll(Context context) {
        Iterator<EventListenerData> iterator = eventListenerList.iterator();
        while (iterator.hasNext()) {
            EventListenerData listenerData = iterator.next();
            if (listenerData.getContext().getClass().equals(context.getClass())) {
                listenerData.getDatabaseReference().removeEventListener(listenerData.getValueEventListener());
                iterator.remove();
            }
        }
    }



}

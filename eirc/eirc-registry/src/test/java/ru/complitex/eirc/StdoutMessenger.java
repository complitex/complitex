package ru.complitex.eirc;

import ru.complitex.eirc.registry.service.AbstractMessenger;
import ru.complitex.eirc.registry.service.RegistryMessenger;

import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.Queue;

/**
 * @author Pavel Sknar
 */
public class StdoutMessenger extends AbstractMessenger {
    private final static String RESOURCE_BUNDLE = RegistryMessenger.class.getName();
    private final static Locale LOCALE = new Locale("ru");

    Queue<IMessage> messages = new Queue<IMessage>() {
        @Override
        public boolean add(IMessage iMessage) {
            System.out.println(iMessage.getLocalizedString(LOCALE));
            return true;
        }

        @Override
        public boolean offer(IMessage iMessage) {
            return false;
        }

        @Override
        public IMessage remove() {
            return null;
        }

        @Override
        public IMessage poll() {
            return null;
        }

        @Override
        public IMessage element() {
            return null;
        }

        @Override
        public IMessage peek() {
            return null;
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public boolean contains(Object o) {
            return false;
        }

        @Override
        public Iterator<IMessage> iterator() {
            return null;
        }

        @Override
        public Object[] toArray() {
            return new Object[0];
        }

        @Override
        public <T> T[] toArray(T[] a) {
            return null;
        }

        @Override
        public boolean remove(Object o) {
            return false;
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return false;
        }

        @Override
        public boolean addAll(Collection<? extends IMessage> c) {
            return false;
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            return false;
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            return false;
        }

        @Override
        public void clear() {

        }
    };

    @Override
    public String getResourceBundle() {
        return RESOURCE_BUNDLE;
    }

    @Override
    public Queue<IMessage> getIMessages() {
        return messages;
    }
}

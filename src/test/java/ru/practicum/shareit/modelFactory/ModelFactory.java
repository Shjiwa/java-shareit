package ru.practicum.shareit.modelFactory;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class ModelFactory {

    private static volatile ModelFactory factory;

    private ModelFactory() {

    }

    public static ModelFactory getInstance() {
        ModelFactory result = factory;

        if (result != null) {
            return result;
        }
        synchronized (ModelFactory.class) {
            if (factory == null) {
                factory = new ModelFactory();
            }
            return factory;
        }
    }

    public User createUser() {
        User user = new User();
        user.setName("user");
        user.setEmail("user@mail.ru");
        return user;
    }

    public Item createItem(User owner) {
        Item item = new Item();
        item.setName("film");
        item.setDescription("film description");
        item.setAvailable(true);
        item.setOwner(owner);
        return item;
    }

    public void setNewUserData(User user) {
        user.setName(user.getName() + Math.random());
        user.setEmail(user.getName() + "@mail.ru");
    }

    public void setItemName(Item item) {
        item.setName(item.getName() + "  " + Math.random());
    }
}
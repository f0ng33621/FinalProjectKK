package repository.memory;

import domain.Customer;
import domain.Menu;
import service.MenuRepository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class InMemoryMenuRepository implements MenuRepository {
    private static long nextMenuId = 0;
    private final Map<String,Menu> repo = new HashMap<>();

    @Override
    public Menu createMenu(String menuName,double price) {
        String MenuId = "M" + ++nextMenuId;
        Menu menu = new Menu(MenuId,menuName,price);
        if (repo.putIfAbsent(MenuId, menu) == null) return menu;
        return null;

    }

    @Override
    public Menu findMenu(String menuCode) {
        return repo.get(menuCode);
    }

    @Override
    public Menu updateMenu(Menu menu) {
        repo.replace(menu.getCode(),menu);
        return menu;
    }

    @Override
    public Collection<Menu> allMenu() {
        return repo.values();
    }

    @Override
    public boolean removeMenu(String menuCode) {
        if (repo.containsKey(menuCode)){
            repo.remove(menuCode);
            return true;
        }
        return false;
    }
}

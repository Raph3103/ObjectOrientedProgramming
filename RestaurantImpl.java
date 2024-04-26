package OOP.Solution;

import OOP.Provided.HungryStudent;
import OOP.Provided.Restaurant;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class RestaurantImpl implements OOP.Provided.Restaurant {
    private final int id;
    private final String name;
    private final int distFromTech;
    private final Set<String> menuItems;
    private final Map<HungryStudent, Integer> ratings;

    public RestaurantImpl(int id, String name, int distFromTech, Set<String> menuItems) {
        this.id = id;
        this.name = name;
        this.distFromTech = distFromTech;
        this.menuItems = new HashSet<>(menuItems);
        ratings = new HashMap<>();
    }

    public static int compareRestaurantByDistAscRateDescIdAsc(Restaurant r1, Restaurant r2) {
        int diffByDistance = r1.distance() - r2.distance();
        if (diffByDistance != 0) {
            return diffByDistance;
        }
        double diffByRating = r1.averageRating() - r2.averageRating();
        if (diffByRating != 0) {
            return diffByRating > 0 ? -1 : 1;
        }
        return ((RestaurantImpl) r1).getId() - ((RestaurantImpl) r2).getId();
    }

    public static int compareRestaurantByRateDescDistAscIdAsc(Restaurant r1, Restaurant r2) {
        double diffByRating = r1.averageRating() - r2.averageRating();
        if (diffByRating != 0) {
            return diffByRating > 0 ? -1 : 1;
        }
        int diffByDistance = r1.distance() - r2.distance();
        if (diffByDistance != 0) {
            return diffByDistance;
        }
        return ((RestaurantImpl) r1).getId() - ((RestaurantImpl) r2).getId();
    }

    @Override
    public int distance() {
        return distFromTech;
    }

    @Override
    public Restaurant rate(HungryStudent s, int r) throws RateRangeException {
        if (r < 0 || r > 5) {
            throw new RateRangeException();
        }
        ratings.put(s, r);
        return this;
    }

    @Override
    public int numberOfRates() {
        return ratings.size();
    }

    @Override
    public double averageRating() {
        if (ratings.isEmpty()) {
            return 0;
        }
        return ratings.values().stream().map(Integer::doubleValue).reduce(Double::sum).get() / ratings.size();
    }

    @Override
    public int compareTo(Restaurant other) {
        return this.id - ((RestaurantImpl) other).id;
    }

    @Override
    public int hashCode() {
        return id * 7;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != this.getClass()) {
            return false;
        }
        return this.id == ((RestaurantImpl) obj).id;
    }

    @Override
    public String toString() {
        String description = String.format("Restaurant: %s.\nId: %d.\nDistance: %d.\nMenu: ", name, id, distFromTech);
        String items = menuItems.stream().sorted().collect(Collectors.joining(", "));
        return description + items + '.';
    }

    public boolean isRatedByStudent(HungryStudent student) {
        return ratings.containsKey(student);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean equalsById(int id) {
        return this.id == id;
    }
}

package OOP.Solution;

import OOP.Provided.HungryStudent;
import OOP.Provided.Restaurant;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class HungryStudentImpl implements HungryStudent {
    private final int id;
    private final String name;
    private final Set<Restaurant> favoriteRestaurants;
    private final Set<HungryStudent> friends;

    public HungryStudentImpl(int id, String name) {
        this.id = id;
        this.name = name;
        this.favoriteRestaurants = new HashSet<>();
        this.friends = new HashSet<>();
    }

    @Override
    public HungryStudent favorite(Restaurant r) throws UnratedFavoriteRestaurantException {
        if (!(((RestaurantImpl) r).isRatedByStudent(this))) {
            throw new UnratedFavoriteRestaurantException();
        }
        favoriteRestaurants.add(r);
        return this;
    }

    @Override
    public Collection<Restaurant> favorites() {
        return new HashSet<>(favoriteRestaurants);
    }

    @Override
    public HungryStudent addFriend(HungryStudent s) throws SameStudentException, ConnectionAlreadyExistsException {
        if (s.equals(this)) {
            throw new SameStudentException();
        }
        if (friends.contains(s)) {
            throw new ConnectionAlreadyExistsException();
        }
        friends.add(s);
        return this;
    }

    @Override
    public Set<HungryStudent> getFriends() {
        return new HashSet<>(friends);
    }

    @Override
    public Collection<Restaurant> favoritesByRating(int rLimit) {
        return favoriteRestaurants.stream().filter(restaurant -> restaurant.averageRating() >= rLimit)
                .sorted(RestaurantImpl::compareRestaurantByRateDescDistAscIdAsc).toList();
    }

    @Override
    public Collection<Restaurant> favoritesByDist(int dLimit) {
        return favoriteRestaurants.stream().filter(restaurant -> restaurant.distance() <= dLimit)
                .sorted(RestaurantImpl::compareRestaurantByDistAscRateDescIdAsc).toList();
    }

    @Override
    public int compareTo(HungryStudent other) {
        return this.id - ((HungryStudentImpl) other).id;
    }

    @Override
    public int hashCode() {
        return this.id * 9;
    }

    @Override
    public boolean equals(Object obj) {
        if (this.getClass() != obj.getClass()) return false;
        return this.id == ((HungryStudentImpl) obj).id;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        String description = String.format("Hungry student: %s.\nId: %d.\nFavorites: ", name, id);
        String rest = favoriteRestaurants.stream().map(r -> ((RestaurantImpl) r).getName()).sorted().collect(Collectors.joining(", "));
        return description + rest + '.';
    }

    public boolean equalsById(int id) {
        return this.id == id;
    }
}

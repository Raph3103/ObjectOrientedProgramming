package OOP.Solution;

import OOP.Provided.HamburgerNetwork;
import OOP.Provided.HungryStudent;
import OOP.Provided.HungryStudent.StudentAlreadyInSystemException;
import OOP.Provided.Restaurant;
import OOP.Provided.Restaurant.RestaurantAlreadyInSystemException;

import java.util.*;
import java.util.stream.Collectors;

public class HamburgerNetworkImpl implements HamburgerNetwork {
    private final Map<HungryStudent, Set<HungryStudent>> connections;
    private final Set<Restaurant> restaurants;

    public HamburgerNetworkImpl() {
        this.connections = new HashMap<>();
        this.restaurants = new HashSet<>();
    }

    @Override
    public HungryStudent joinNetwork(int id, String name) throws StudentAlreadyInSystemException {
        HungryStudent student = new HungryStudentImpl(id, name);
        if (connections.containsKey(student)) {
            throw new StudentAlreadyInSystemException();
        }
        connections.put(student, new HashSet<>());
        return student;
    }

    @Override
    public Restaurant addRestaurant(int id, String name, int dist, Set<String> menu) throws Restaurant.RestaurantAlreadyInSystemException {
        Restaurant restaurant = new RestaurantImpl(id, name, dist, menu);
        if (restaurants.contains(restaurant)) {
            throw new RestaurantAlreadyInSystemException();
        }
        restaurants.add(restaurant);
        return restaurant;
    }

    @Override
    public Collection<HungryStudent> registeredStudents() {
        return new HashSet<>(connections.keySet());
    }

    @Override
    public Collection<Restaurant> registeredRestaurants() {
        return new HashSet<>(restaurants);
    }

    @Override
    public HungryStudent getStudent(int id) throws HungryStudent.StudentNotInSystemException {
        HungryStudent student = connections.keySet().stream().filter(s -> ((HungryStudentImpl) s).equalsById(id)).findFirst().orElse(null);
        if (student == null) {
            throw new HungryStudent.StudentNotInSystemException();
        }
        return student;
    }

    @Override
    public Restaurant getRestaurant(int id) throws Restaurant.RestaurantNotInSystemException {
        Restaurant restaurant = restaurants.stream().filter(r -> ((RestaurantImpl) r).equalsById(id)).findFirst().orElse(null);
        if (restaurant == null) {
            throw new Restaurant.RestaurantNotInSystemException();
        }
        return restaurant;
    }

    @Override
    public HamburgerNetwork addConnection(HungryStudent s1, HungryStudent s2) throws HungryStudent.StudentNotInSystemException, HungryStudent.ConnectionAlreadyExistsException, HungryStudent.SameStudentException {
        if (!connections.containsKey(s1) || !connections.containsKey(s2)) {
            throw new HungryStudent.StudentNotInSystemException();
        }
        if (s1.equals(s2)) {
            throw new HungryStudent.SameStudentException();
        }
        if (connections.get(s1).contains(s2) || connections.get(s2).contains(s1)) {
            throw new HungryStudent.ConnectionAlreadyExistsException();
        }
        connections.get(s1).add(s2);
        connections.get(s2).add(s1);
        s1.addFriend(s2);
        s2.addFriend(s1);
        return this;
    }

    @Override
    public Collection<Restaurant> favoritesByRating(HungryStudent s) throws HungryStudent.StudentNotInSystemException {
        if (!connections.containsKey(s)) {
            throw new HungryStudent.StudentNotInSystemException();
        }
        return connections.get(s).stream().sorted()
                .map(HungryStudent::favorites)
                .flatMap(restaurants -> restaurants.stream().sorted(RestaurantImpl::compareRestaurantByRateDescDistAscIdAsc))
                .distinct().toList();
    }

    @Override
    public Collection<Restaurant> favoritesByDist(HungryStudent s) throws HungryStudent.StudentNotInSystemException {
        if (!connections.containsKey(s)) {
            throw new HungryStudent.StudentNotInSystemException();
        }
        return connections.get(s).stream().sorted()
                .map(HungryStudent::favorites)
                .flatMap(restaurants -> restaurants.stream().sorted(RestaurantImpl::compareRestaurantByDistAscRateDescIdAsc))
                .distinct().toList();
    }

    @Override
    public boolean getRecommendation(HungryStudent s, Restaurant r, int t)
            throws HungryStudent.StudentNotInSystemException, Restaurant.RestaurantNotInSystemException, ImpossibleConnectionException {
        if (!connections.containsKey(s)) {
            throw new HungryStudent.StudentNotInSystemException();
        }
        if (!restaurants.contains(r)) {
            throw new Restaurant.RestaurantNotInSystemException();
        }
        if (t < 0) {
            throw new ImpossibleConnectionException();
        }
        Queue<HungryStudent> q = new ArrayDeque<>();
        q.add(s);
        int currentLevel = 0;
        while (!q.isEmpty() && currentLevel <= t) {
            int levelSize = q.size();
            for (int i = 0; i < levelSize; i++) {
                HungryStudent currentStudent = q.remove();
                if (currentStudent.favorites().contains(r)) {
                    return true;
                }
                q.addAll(connections.get(currentStudent));
            }
            currentLevel++;
        }
        return false;
    }

    @Override
    public String toString() {
        String registeredStudents = "Registered students: " +
                connections.keySet().stream()
                        .map(s -> Integer.toString(((HungryStudentImpl) s).getId()))
                        .sorted()
                        .collect(Collectors.joining(", ")) + ".\n";
        String registeredRestaurants = "Registered restaurants: " +
                restaurants.stream()
                        .map(r -> Integer.toString(((RestaurantImpl) r).getId()))
                        .sorted()
                        .collect(Collectors.joining(", ")) + ".\n";
        String studentAndFriends;
        if (connections.keySet().isEmpty()) {
             studentAndFriends = "Students:\nEnd students.";
        } else {
             studentAndFriends = "Students:\n" +
                    connections.keySet().stream()
                            .sorted()
                            .map(s ->
                                    ((HungryStudentImpl) s).getId() + " -> [" +
                                            connections.get(s).stream()
                                                    .sorted()
                                                    .map(f -> Integer.toString(((HungryStudentImpl) f).getId()))
                                                    .collect(Collectors.joining(", "))
                                            + "].")
                            .collect(Collectors.joining("\n")) + "\nEnd students.";
        }
        return registeredStudents + registeredRestaurants + studentAndFriends;
    }
}

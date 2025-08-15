//package Engine;
//
//import java.util.Map;
//import java.util.Optional;
//import java.util.function.Function;
//
//public class MapSearcher {
//
//    static public <T> T findOrCreate(Map<String, T> map,
//                              Optional<String> key,
//                              Function<String, T> creator) {
//        return key.map(keyName ->
//                map.computeIfAbsent(keyName, creator)
//        );
//    }
//}
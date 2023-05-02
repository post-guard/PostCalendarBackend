package top.rrricardo.postcalendarbackend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import top.rrricardo.postcalendarbackend.annotations.Authorize;
import top.rrricardo.postcalendarbackend.dtos.ResponseDTO;
import top.rrricardo.postcalendarbackend.enums.AuthorizePolicy;
import top.rrricardo.postcalendarbackend.mappers.PlaceMapper;
import top.rrricardo.postcalendarbackend.models.Place;
import top.rrricardo.postcalendarbackend.utils.ControllerBase;

import java.util.List;

@RestController
@RequestMapping("/place")
public class PlaceController extends ControllerBase {

    private final PlaceMapper placeMapper;

    public PlaceController(PlaceMapper placeMapper) {
        this.placeMapper = placeMapper;
    }

    @GetMapping("/")
    @Authorize(policy = AuthorizePolicy.ONLY_LOGIN)
    public ResponseEntity<ResponseDTO<List<Place>>> getPlaces(){
        var places = placeMapper.getPlaces();

        return ok(places);
    }
    @GetMapping("/{id}")
    @Authorize(policy = AuthorizePolicy.ONLY_LOGIN)
    public ResponseEntity<ResponseDTO<Place>> getPlace(@PathVariable(value = "id") int id) {
        var place = placeMapper.getPlaceById(id);

        if (place == null) {
            return notFound("地点不存在");
        }

        return ok(place);
    }


    @PostMapping("/")
    @Authorize(policy = AuthorizePolicy.ABOVE_ADMINISTRATOR)
    public ResponseEntity<ResponseDTO<Place>> createPlace(@RequestBody Place place){

        placeMapper.createPlace(place);

        return created();

    }

    @PutMapping("/{id}")
    @Authorize(policy = AuthorizePolicy.ABOVE_ADMINISTRATOR)
    public ResponseEntity<ResponseDTO<Place>> updatePlace
            (@PathVariable (value = "id") int id, @RequestBody Place place) throws NullPointerException{

        if (id != place.getId()) {
            return badRequest();
        }

        var oldPlace = placeMapper.getPlaceById(id);
        if(oldPlace == null){
            //地点不存在
            return notFound("地点不存在");
        }

        placeMapper.updatePlace(place);

        var newPlace = placeMapper.getPlaceById(id);

        if(newPlace == null){
            throw new NullPointerException();
        }

        return ok(newPlace);
    }

    @DeleteMapping("/{id}")
    @Authorize(policy = AuthorizePolicy.ABOVE_ADMINISTRATOR)
    public ResponseEntity<ResponseDTO<Place>> deletePlace(@PathVariable(value = "id") int id){

        var place = placeMapper.getPlaceById(id);

        if (place == null) {
            return notFound("地点不存在");
        }

        placeMapper.deletePlace(id);

        return noContent();
    }
}

package top.rrricardo.postcalendarbackend.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import top.rrricardo.postcalendarbackend.annotations.Authorize;
import top.rrricardo.postcalendarbackend.dtos.ResponseDTO;
import top.rrricardo.postcalendarbackend.enums.AuthorizePolicy;
import top.rrricardo.postcalendarbackend.mappers.PlaceMapper;
import top.rrricardo.postcalendarbackend.models.Place;
import top.rrricardo.postcalendarbackend.services.NavigationService;
import top.rrricardo.postcalendarbackend.utils.ControllerBase;

import java.util.List;

@RestController
@RequestMapping("/place")
public class PlaceController extends ControllerBase {

    private final PlaceMapper placeMapper;
    private final NavigationService navigationService;
    private final Logger logger;
    public PlaceController(PlaceMapper placeMapper, NavigationService navigationService) {
        this.placeMapper = placeMapper;
        this.navigationService = navigationService;
        this.logger = LoggerFactory.getLogger(PlaceController.class);
    }

    @GetMapping("/")
    @Authorize(policy = AuthorizePolicy.ONLY_LOGIN)
    public ResponseEntity<ResponseDTO<List<Place>>> getPlaces(){
        var places = placeMapper.getPlaces();

        logger.info("获取地点列表成功");
        return ok(places);
    }
    @GetMapping("/{id}")
    @Authorize(policy = AuthorizePolicy.ONLY_LOGIN)
    public ResponseEntity<ResponseDTO<Place>> getPlace(@PathVariable(value = "id") int id) {
        var place = placeMapper.getPlaceById(id);

        if (place == null) {
            logger.info("获取地点失败，id={}的地点不存在", id);
            return notFound("地点不存在");
        }

        logger.info("获取地点成功，id={}",id);
        return ok(place);
    }


    @PostMapping("/")
    @Authorize(policy = AuthorizePolicy.ABOVE_ADMINISTRATOR)
    public ResponseEntity<ResponseDTO<Place>> createPlace(@RequestBody Place place){

        placeMapper.createPlace(place);
        navigationService.setMapUpdated();

        logger.info("创建地点成功");
        return created(place);
    }

    @PutMapping("/{id}")
    @Authorize(policy = AuthorizePolicy.ABOVE_ADMINISTRATOR)
    public ResponseEntity<ResponseDTO<Place>> updatePlace
            (@PathVariable (value = "id") int id, @RequestBody Place place) throws NullPointerException{

        if (id != place.getId()) {
            logger.info("更新地点失败, id不一致");
            return badRequest();
        }

        var oldPlace = placeMapper.getPlaceById(id);
        if(oldPlace == null){
            //地点不存在
            logger.info("更新地点失败，id={}的地点不存在", id);
            return notFound("地点不存在");
        }

        placeMapper.updatePlace(place);
        navigationService.setMapUpdated();

        var newPlace = placeMapper.getPlaceById(id);

        if(newPlace == null){
            throw new NullPointerException();
        }

        logger.info("更新地点成功, 地点id={}", id);
        return ok(newPlace);
    }

    @DeleteMapping("/{id}")
    @Authorize(policy = AuthorizePolicy.ABOVE_ADMINISTRATOR)
    public ResponseEntity<ResponseDTO<Place>> deletePlace(@PathVariable(value = "id") int id){

        var place = placeMapper.getPlaceById(id);

        if (place == null) {
            logger.info("删除失败，id={}的地点不存在", id);
            return notFound("地点不存在");
        }

        placeMapper.deletePlace(id);
        navigationService.setMapUpdated();

        logger.info("删除地点成功,id={}", id);
        return noContent();
    }
}

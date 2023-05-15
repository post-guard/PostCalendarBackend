package top.rrricardo.postcalendarbackend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import top.rrricardo.postcalendarbackend.annotations.Authorize;
import top.rrricardo.postcalendarbackend.dtos.ResponseDTO;
import top.rrricardo.postcalendarbackend.enums.AuthorizePolicy;
import top.rrricardo.postcalendarbackend.mappers.RoadMapper;
import top.rrricardo.postcalendarbackend.models.Road;
import top.rrricardo.postcalendarbackend.services.NavigationService;
import top.rrricardo.postcalendarbackend.utils.ControllerBase;

import java.util.List;

@RestController
@RequestMapping("/road")
public class RoadController extends ControllerBase {

    private final RoadMapper roadMapper;
    private final NavigationService navigationService;


    public RoadController(RoadMapper roadMapper, NavigationService navigationService) {
        this.roadMapper = roadMapper;
        this.navigationService = navigationService;
    }

    @GetMapping("/")
    @Authorize(policy = AuthorizePolicy.ONLY_LOGIN)
    public ResponseEntity<ResponseDTO<List<Road>>> getRoads(){
        var roads = roadMapper.getRoads();

        return ok(roads);
    }

    @GetMapping("/{id}")
    @Authorize(policy = AuthorizePolicy.ONLY_LOGIN)
    public ResponseEntity<ResponseDTO<Road>> getRoad(@PathVariable(value = "id") int id) {
        var road = roadMapper.getRoadById(id);

        if (road == null) {
            return notFound("道路不存在");
        }

        return ok(road);
    }

    @PostMapping("/")
    @Authorize(policy = AuthorizePolicy.ABOVE_ADMINISTRATOR)
    public ResponseEntity<ResponseDTO<Road>> createRoad(@RequestBody Road road){

        roadMapper.createRoad(road);
        navigationService.setMapUpdated();

        return created(road);
    }

    @PutMapping("/{id}")
    @Authorize(policy = AuthorizePolicy.ABOVE_ADMINISTRATOR)
    public ResponseEntity<ResponseDTO<Road>> updateRoad
            (@PathVariable (value = "id") int id, @RequestBody Road road) throws NullPointerException{

        if (id != road.getId()) {
            return badRequest();
        }

        var oldRoad = roadMapper.getRoadById(id);
        if(oldRoad == null){
            //道路不存在
            return notFound("道路不存在");
        }

        roadMapper.updateRoad(road);
        navigationService.setMapUpdated();

        var newRoad = roadMapper.getRoadById(id);

        if(newRoad == null){
            throw new NullPointerException();
        }

        return ok(newRoad);
    }

    @DeleteMapping("/{id}")
    @Authorize(policy = AuthorizePolicy.ABOVE_ADMINISTRATOR)
    public ResponseEntity<ResponseDTO<Road>> deleteRoad(@PathVariable(value = "id") int id){

        var road = roadMapper.getRoadById(id);

        if (road == null) {
            return notFound("道路不存在");
        }

        roadMapper.deleteRoad(id);
        navigationService.setMapUpdated();

        return noContent();
    }


}

package top.rrricardo.postcalendarbackend.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final Logger logger;

    public RoadController(RoadMapper roadMapper, NavigationService navigationService) {
        this.roadMapper = roadMapper;
        this.navigationService = navigationService;
        this.logger = LoggerFactory.getLogger(RoadController.class);
    }

    @GetMapping("/")
    @Authorize(policy = AuthorizePolicy.ONLY_LOGIN)
    public ResponseEntity<ResponseDTO<List<Road>>> getRoads(){
        var roads = roadMapper.getRoads();

        logger.info("获取所有道路成功");
        return ok(roads);
    }

    @GetMapping("/{id}")
    @Authorize(policy = AuthorizePolicy.ONLY_LOGIN)
    public ResponseEntity<ResponseDTO<Road>> getRoad(@PathVariable(value = "id") int id) {
        var road = roadMapper.getRoadById(id);

        if (road == null) {
            logger.info("获取道路失败，不存在id={}的道路",id);
            return notFound("道路不存在");
        }

        logger.info("获取道路成功，id={}",id);
        return ok(road);
    }

    @PostMapping("/")
    @Authorize(policy = AuthorizePolicy.ABOVE_ADMINISTRATOR)
    public ResponseEntity<ResponseDTO<Road>> createRoad(@RequestBody Road road){

        roadMapper.createRoad(road);
        navigationService.setMapUpdated();

        logger.info("创建道路成功,id={}", road.getId());
        return created(road);
    }

    @PutMapping("/{id}")
    @Authorize(policy = AuthorizePolicy.ABOVE_ADMINISTRATOR)
    public ResponseEntity<ResponseDTO<Road>> updateRoad
            (@PathVariable (value = "id") int id, @RequestBody Road road) throws NullPointerException{

        if (id != road.getId()) {
            logger.info("更新道路失败，id不一致");
            return badRequest();
        }

        var oldRoad = roadMapper.getRoadById(id);
        if(oldRoad == null){
            //道路不存在
            logger.info("更新道路失败，id={}的道路不存在", id);
            return notFound("道路不存在");
        }

        roadMapper.updateRoad(road);
        navigationService.setMapUpdated();

        var newRoad = roadMapper.getRoadById(id);

        if(newRoad == null){
            throw new NullPointerException();
        }

        logger.info("更新道路成功，id={}",id);
        return ok(newRoad);
    }

    @DeleteMapping("/{id}")
    @Authorize(policy = AuthorizePolicy.ABOVE_ADMINISTRATOR)
    public ResponseEntity<ResponseDTO<Road>> deleteRoad(@PathVariable(value = "id") int id){

        var road = roadMapper.getRoadById(id);

        if (road == null) {
            logger.info("删除道路失败，不存在id={}的道路", id);
            return notFound("道路不存在");
        }

        roadMapper.deleteRoad(id);
        navigationService.setMapUpdated();

        logger.info("删除道路成功，id={}", id);
        return noContent();
    }


}

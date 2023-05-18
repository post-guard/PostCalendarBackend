package top.rrricardo.postcalendarbackend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import top.rrricardo.postcalendarbackend.annotations.Authorize;
import top.rrricardo.postcalendarbackend.dtos.NavigationDTO;
import top.rrricardo.postcalendarbackend.dtos.ResponseDTO;
import top.rrricardo.postcalendarbackend.enums.AuthorizePolicy;
import top.rrricardo.postcalendarbackend.mappers.PlaceMapper;
import top.rrricardo.postcalendarbackend.services.NavigationService;
import top.rrricardo.postcalendarbackend.utils.ControllerBase;
import top.rrricardo.postcalendarbackend.utils.generic.CustomList;

import java.util.List;

@RestController
@RequestMapping("/navigation")
public class NavigationController extends ControllerBase {
    private final NavigationService navigationService;
    private final PlaceMapper placeMapper;

    public NavigationController(NavigationService navigationService, PlaceMapper placeMapper) {
        this.navigationService = navigationService;
        this.placeMapper = placeMapper;
    }

    @GetMapping("/oneDestination")
    @Authorize(policy = AuthorizePolicy.ONLY_LOGIN)
    public ResponseEntity<ResponseDTO<NavigationDTO>> getNavigation(
        @RequestParam int startPlaceId, @RequestParam int endPlaceId
    ) {
        // 首先判断请求的地点是否存在
        var startPlace = placeMapper.getPlaceById(startPlaceId);
        var endPlace = placeMapper.getPlaceById(endPlaceId);

        if (startPlace == null || endPlace == null) {
            return badRequest("请求的地点不存在");
        }

        var placeList = navigationService.findPathOneDestination(startPlaceId, endPlaceId);
        var roadList = navigationService.getRoadsByPlace(placeList);

        var response = new NavigationDTO(placeList.toList(), roadList.toList());
        return ok(response);
    }

    @GetMapping("/")
    @Authorize(policy = AuthorizePolicy.ONLY_LOGIN)
    public ResponseEntity<ResponseDTO<NavigationDTO>> getNavigationMany(
            @RequestBody List<Integer> places
    ) {
        if (places.size() < 2) {
            return badRequest("请求的地点数过少");
        }

        var placeList = navigationService.findPathManyDestination(new CustomList<>(places));
        var roadList = navigationService.getRoadsByPlace(placeList);

        var response = new NavigationDTO(placeList.toList(), roadList.toList());

        return ok(response);
    }
}

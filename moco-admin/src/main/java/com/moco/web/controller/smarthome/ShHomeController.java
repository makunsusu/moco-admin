package com.moco.web.controller.smarthome;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.moco.common.core.controller.BaseController;
import com.moco.common.core.page.TableDataInfo;
import com.moco.system.domain.ShHome;
import com.moco.system.domain.ShRoom;
import com.moco.system.service.IShHomeService;

@RestController
@RequestMapping("/smarthome")
public class ShHomeController extends BaseController
{
    @Autowired
    private IShHomeService homeService;

    @PreAuthorize("@ss.hasPermi('smarthome:home:list')")
    @GetMapping("/home/list")
    public TableDataInfo listHomes(ShHome home)
    {
        startPage();
        return getDataTable(homeService.selectHomeList(home));
    }

    @PreAuthorize("@ss.hasPermi('smarthome:home:list')")
    @GetMapping("/room/list")
    public TableDataInfo listRooms(ShRoom room)
    {
        startPage();
        return getDataTable(homeService.selectRoomList(room));
    }
}

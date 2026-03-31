<template>
  <div :class="classObj" class="app-wrapper" :style="{'--current-color': theme, '--current-color-light': theme + '1a', '--current-color-dark-bg': theme + '33'}">
    <div v-if="device==='mobile'&&sidebar.opened" class="drawer-bg" @click="handleClickOutside"/>
    <sidebar v-if="!sidebar.hide" class="sidebar-container"/>
    <div :class="{hasTagsView:needTagsView,sidebarHide:sidebar.hide}" class="main-container">
      <div :class="{'fixed-header':fixedHeader}">
        <navbar @setLayout="setLayout"/>
        <tags-view v-if="needTagsView"/>
      </div>
      <app-main/>
      <settings ref="settingRef"/>
    </div>
  </div>
</template>

<script>
import { AppMain, Navbar, Settings, Sidebar, TagsView } from './components'
import ResizeMixin from './mixin/ResizeHandler'
import { mapState } from 'vuex'
import variables from '@/assets/styles/variables.scss'

export default {
  name: 'Layout',
  components: {
    AppMain,
    Navbar,
    Settings,
    Sidebar,
    TagsView
  },
  mixins: [ResizeMixin],
  computed: {
    ...mapState({
      theme: state => state.settings.theme,
      sideTheme: state => state.settings.sideTheme,
      sidebar: state => state.app.sidebar,
      device: state => state.app.device,
      needTagsView: state => state.settings.tagsView,
      fixedHeader: state => state.settings.fixedHeader
    }),
    classObj() {
      return {
        hideSidebar: !this.sidebar.opened,
        openSidebar: this.sidebar.opened,
        withoutAnimation: this.sidebar.withoutAnimation,
        mobile: this.device === 'mobile'
      }
    },
    variables() {
      return variables
    }
  },
  methods: {
    handleClickOutside() {
      this.$store.dispatch('app/closeSideBar', { withoutAnimation: false })
    },
    setLayout() {
      this.$refs.settingRef.openSetting()
    }
  }
}
</script>

<style lang="scss" scoped>
  @import "~@/assets/styles/mixin.scss";
  @import "~@/assets/styles/variables.scss";

  .app-wrapper {
    @include clearfix;
    position: relative;
    height: 100%;
    width: 100%;
    background:
      radial-gradient(circle at 8% 18%, rgba(15, 184, 184, 0.14), transparent 18%),
      radial-gradient(circle at 88% 10%, rgba(27, 108, 168, 0.2), transparent 24%),
      linear-gradient(180deg, #fbfdff 0%, #f3f8ff 44%, #edf5ff 100%);
    overflow: hidden;

    &::before,
    &::after {
      content: "";
      position: absolute;
      pointer-events: none;
    }

    &::before {
      inset: 0;
      background-image:
        linear-gradient(rgba(27, 108, 168, 0.035) 1px, transparent 1px),
        linear-gradient(90deg, rgba(27, 108, 168, 0.035) 1px, transparent 1px);
      background-size: 36px 36px;
      mask-image: linear-gradient(180deg, rgba(0, 0, 0, 0.4), transparent 85%);
      opacity: 0.32;
    }

    &::after {
      top: -120px;
      right: -80px;
      width: 360px;
      height: 360px;
      background: radial-gradient(circle, rgba(56, 189, 248, 0.24), rgba(56, 189, 248, 0));
    }

    &.mobile.openSidebar {
      position: fixed;
      top: 0;
    }
  }

  .main-container:has(.fixed-header) {
    height: 100vh;
    overflow: hidden;
  }

  .drawer-bg {
    background: rgba(9, 20, 36, 0.42);
    backdrop-filter: blur(8px);
    width: 100%;
    top: 0;
    height: 100%;
    position: absolute;
    z-index: 999;
  }

  .fixed-header {
    position: fixed;
    top: 0;
    right: 0;
    z-index: 9;
    width: calc(100% - #{$base-sidebar-width});
    transition: width 0.28s;
  }

  .hideSidebar .fixed-header {
    width: calc(100% - 54px);
  }

  .sidebarHide .fixed-header {
    width: 100%;
  }

  .mobile .fixed-header {
    width: 100%;
  }
</style>

<template>
  <footer v-if="visible" class="copyright">
    <span>{{ content }}</span>
  </footer>
</template>

<script>
export default {
  computed: {
    visible() {
      return this.$store.state.settings.footerVisible
    },
    content() {
      return this.$store.state.settings.footerContent
    }
  }
}
</script>

<style scoped>
.copyright {
  position: fixed;
  bottom: 0;
  left: 20px;
  right: 20px;
  height: 42px;
  padding: 12px 18px;
  text-align: right;
  background: rgba(255, 252, 246, 0.84);
  color: rgba(16, 37, 63, 0.66);
  font-size: 13px;
  border: 1px solid rgba(214, 189, 158, 0.35);
  border-radius: 18px 18px 0 0;
  box-shadow: 0 -12px 28px rgba(15, 33, 56, 0.08);
  backdrop-filter: blur(10px);
  z-index: 999;
}
</style>

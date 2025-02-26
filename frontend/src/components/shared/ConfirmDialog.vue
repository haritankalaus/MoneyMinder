<template>
  <v-dialog v-model="dialog" max-width="400">
    <v-card>
      <v-card-title class="d-flex align-center">
        <v-icon color="warning" class="mr-2">mdi-alert-circle</v-icon>
        {{ title }}
      </v-card-title>

      <v-card-text class="pt-4">
        {{ message }}
      </v-card-text>

      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn
          variant="outlined"
          @click="onCancel"
          min-width="100"
          class="mr-2"
        >
          Cancel
        </v-btn>
        <v-btn
          color="error"
          variant="elevated"
          @click="onConfirm"
          min-width="100"
        >
          Delete
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
interface Props {
  modelValue: boolean;
  title?: string;
  message: string;
}

const props = withDefaults(defineProps<Props>(), {
  title: 'Confirm Action',
});

const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean): void;
  (e: 'confirm'): void;
  (e: 'cancel'): void;
}>();

const dialog = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value),
});

const onConfirm = () => {
  emit('confirm');
  dialog.value = false;
};

const onCancel = () => {
  emit('cancel');
  dialog.value = false;
};
</script>


<template>
    <div>
      <v-menu
        v-model="menu"
        :close-on-content-click="false"
        close-on-click
        transition="scale-transition"
        offset-y
      >
        <template v-slot:activator="{ props }">
            <v-text-field
            v-model="dateRangeText"
            label="Select date"
            append-icon="mdi-calendar"
            readonly
            v-bind="props"
          ></v-text-field>
        </template>
  
        <v-date-picker
          hide-header
          v-model="dates"
          class="datePicker mt-4"
          show-adjacent-months
          show-current
          scrollable
          rounded
          elevation="5"
          :max="new Date()"
        >
          <template #actions>
            <v-btn color="primary" variant="text" @click="cancel" :text="$t('cancel')" />
            <v-btn color="primary" variant="text" @click="save" :text="$t('ok')" />
          </template>
        </v-date-picker>
      </v-menu>
    </div>
  </template>
  <style>
  .datePicker {
  
    border-radius: 20px !important;
  
  /*  .v-date-picker-month__day--selected {
      background-color: #384b4e;
    }
  
    .v-date-picker-month__day--selected .v-btn,
    .v-date-picker-month__day .v-btn {
      background-color: transparent !important;
      color: white !important;
    }
  
    :nth-child(1 of .v-date-picker-month__day--selected) {
      border-radius: 50% 0 0 50%;
      .v-btn {
        background-color: #359c97 !important;
      }
    }
  
    :nth-last-child(1 of .v-date-picker-month__day--selected) {
      border-radius: 0 50% 50% 0;
      .v-btn {
        background-color: #359c97 !important;
      }
    }
  
    :nth-child(1 of .v-date-picker-month__day--selected):nth-last-child(1
        of
        .v-date-picker-month__day--selected) {
      border-radius: 50% 50% 50% 50%;
      .v-btn {
        background-color: #359c97 !important;
      }
    }*/
  }
  </style>
  
  <script setup lang="ts">
  import { computed, ref, unref } from "vue";
  import { useI18n } from "vue-i18n";
  
  const { t } = useI18n();
  const range = ref<Date[]>([]);
  const menu = ref(false);
  const model = defineModel<Date[]>();
  const dates = computed({
    get: datesBetween,
    set: updateRange,
  });
  
  const dateRangeText = computed(() => {
    if (!model.value?.length) {
      return t("");
    }
    const modelValue = unref(model);
    if (!modelValue) return t("");
    
    const [start, end] = modelValue;
    if (!!start && !!end) {
      return `${t("")} ${start.toLocaleDateString()} ${t(
        "-"
      )} ${end.toLocaleDateString()}`;
    } else if (isToday(start)) {
      return t("");
    } else {
      return start.toLocaleDateString();
    }
  });
  
  function updateRange(date: Date) {
    const rangeValue = unref(range);
    if (!rangeValue || !Array.isArray(rangeValue)) {
      range.value = [date];
      return;
    }
    
    const [start, end] = rangeValue;
    //if everything is null or everything is not
    if (!!start === !!end) {
      range.value = [date];
    } else if (date < start) {
      range.value = [date, start];
    } else if (date > start) {
      range.value = [start, date];
    }
  }
  
  function datesBetween() {
    if (range.value == undefined) {
      return [];
    }
    const [start, end] = unref(range);
    if (!!start && !!end) {
      const between = [];
      const currentDate = new Date(start);
      while (currentDate <= end) {
        between.push(new Date(currentDate));
        currentDate.setDate(currentDate.getDate() + 1);
      }
      return between;
    } else {
      return range.value;
    }
  }
  
  function isToday(date: Date) {
    const today = new Date(Date.now());
    today.setHours(0, 0, 0, 0);
    return today.getTime() == date.getTime();
  }
  
  function save() {
    model.value = range.value;
    menu.value = false;
  }
  
  function cancel() {
    range.value = model.value || [];
    menu.value = false;
  }
  </script>
  
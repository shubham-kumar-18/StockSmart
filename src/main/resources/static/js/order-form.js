document.addEventListener('DOMContentLoaded', () => {
  const rows = document.getElementById('order-lines');
  const template = document.getElementById('line-template');
  const reindex = () => rows.querySelectorAll('.order-input-row').forEach((row, index) => {
    row.querySelectorAll('[data-field]').forEach((field) => {
      field.name = `items[${index}].${field.dataset.field}`;
    });
  });

  document.getElementById('add-line').addEventListener('click', () => {
    if (rows.querySelectorAll('.order-input-row').length >= 30) return;
    const fragment = template.content.cloneNode(true);
    rows.appendChild(fragment);
    reindex();
  });

  rows.addEventListener('click', (event) => {
    if (event.target.closest('.remove-line') && rows.querySelectorAll('.order-input-row').length > 1) {
      event.target.closest('.order-input-row').remove();
      reindex();
    }
  });
});

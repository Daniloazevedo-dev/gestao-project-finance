$(document).ready(function () {
  const formatCurrency = (value) =>
    new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL'
    }).format(value);

  const showFormFeedback = (message, type) => {
    const feedback = $('#formFeedback');
    feedback.removeClass('success error');
    if (!message) {
      feedback.text('');
      return;
    }
    feedback.addClass(type === 'success' ? 'success' : 'error');
    feedback.text(message);
  };

  const parseDecimal = (value) => {
    if (typeof value !== 'string') {
      return Number.NaN;
    }
    const normalized = value.trim().replace(/\s/g, '').replace(',', '.');
    if (normalized.length === 0) {
      return Number.NaN;
    }
    return Number(normalized);
  };

  const toggleRemainingField = () => {
    const remainingInput = $('#expenseRemaining');
    if ($('#expensePaid').is(':checked')) {
      remainingInput.val('0.00');
      remainingInput.prop('disabled', true);
    } else {
      remainingInput.prop('disabled', false);
      if (!remainingInput.val()) {
        const amount = parseDecimal($('#expenseAmount').val());
        if (!Number.isNaN(amount) && amount >= 0) {
          remainingInput.val(amount.toFixed(2));
        }
      }
    }
  };

  const updateSummary = (summary) => {
    $('#plannedValue').text(formatCurrency(summary.totalPlanned));
    $('#paidValue').text(formatCurrency(summary.totalPaid));
    $('#remainingValue').text(formatCurrency(summary.totalRemaining));

    const progress = summary.totalPlanned === 0
      ? 0
      : Math.min((summary.totalPaid / summary.totalPlanned) * 100, 100);

    $('#progressBar').css('width', `${progress}%`);
    $('#progressLabel').text(`${progress.toFixed(0)}% do orçamento quitado`);
  };

  const renderExpenses = (expenses) => {
    const tbody = $('#expensesBody');
    tbody.empty();

    expenses.forEach((expense) => {
      const badgeClass = expense.paid ? 'paid' : 'pending';
      const badgeText = expense.paid ? 'Pago' : 'Pendente';

      const row = `<tr>
        <td><span class="badge ${badgeClass}">${badgeText}</span></td>
        <td>${expense.description}</td>
        <td>${formatCurrency(expense.amount)}</td>
        <td>Dia ${expense.dueDay}</td>
        <td>${formatCurrency(expense.remaining)}</td>
      </tr>`;

      tbody.append(row);
    });
  };

  const renderGoals = (summary) => {
    const goals = [
      {
        title: 'Manter margem de segurança',
        description:
          `Reserve pelo menos ${formatCurrency(summary.totalRemaining)} para despesas variáveis e emergências.`
      },
      {
        title: 'Planejar quitação do cartão',
        description:
          'Defina um plano semanal para pagar o cartão Nubank antes do vencimento e evitar juros.'
      },
      {
        title: 'Monitorar assinaturas',
        description:
          'Revise serviços como internet e streaming buscando renegociação ou ajustes de pacote.'
      }
    ];

    const goalList = $('#goalList');
    goalList.empty();

    goals.forEach((goal) => {
      const item = `<div class="goal-item">
        <strong>${goal.title}</strong>
        <p>${goal.description}</p>
      </div>`;

      goalList.append(item);
    });
  };

  const loadDashboard = () => {
    $.getJSON('/api/finance/summary')
      .done((data) => {
        showFormFeedback('', 'success');
        updateSummary(data.summary);
        renderExpenses(data.expenses);
        renderGoals(data.summary);
      })
      .fail(() => {
        showFormFeedback('Não foi possível carregar os dados do orçamento.', 'error');
        const tbody = $('#expensesBody');
        tbody.empty();
        tbody.append('<tr><td colspan="5">Não foi possível carregar as despesas.</td></tr>');
      });
  };

  const setupForm = () => {
    $('#expensePaid').on('change', toggleRemainingField);

    $('#expenseAmount').on('input', () => {
      if (!$('#expensePaid').is(':checked')) {
        const amount = parseDecimal($('#expenseAmount').val());
        if (!Number.isNaN(amount) && amount >= 0) {
          $('#expenseRemaining').val(amount.toFixed(2));
        }
      }
    });

    $('#expenseForm').on('submit', function (event) {
      event.preventDefault();

      const description = $('#expenseDescription').val().trim();
      const amount = parseDecimal($('#expenseAmount').val());
      const dueDay = Number($('#expenseDueDay').val());
      const paid = $('#expensePaid').is(':checked');
      let remaining = parseDecimal($('#expenseRemaining').val());

      if (!description) {
        showFormFeedback('Informe uma descrição para a despesa.', 'error');
        return;
      }

      if (Number.isNaN(amount) || amount <= 0) {
        showFormFeedback('Informe um valor válido maior que zero.', 'error');
        return;
      }

      if (!Number.isInteger(dueDay) || dueDay < 1 || dueDay > 31) {
        showFormFeedback('O dia de vencimento deve estar entre 1 e 31.', 'error');
        return;
      }

      if (paid) {
        remaining = 0;
      } else if (Number.isNaN(remaining) || remaining < 0) {
        showFormFeedback('Informe um valor restante válido.', 'error');
        return;
      }

      const payload = {
        paid,
        description,
        amount: Number(amount.toFixed(2)),
        dueDay,
        remaining: Number(remaining.toFixed(2))
      };

      $.ajax({
        url: '/api/finance/expenses',
        method: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(payload)
      })
        .done(() => {
          showFormFeedback('Despesa cadastrada com sucesso!', 'success');
          event.target.reset();
          $('#expenseRemaining').prop('disabled', false).val('');
          toggleRemainingField();
          loadDashboard();
        })
        .fail((xhr) => {
          let message = 'Não foi possível cadastrar a despesa. Tente novamente.';
          const response = xhr.responseJSON;
          if (response) {
            if (Array.isArray(response.errors) && response.errors.length > 0) {
              message = response.errors.join(', ');
            } else if (response.errors && typeof response.errors === 'object') {
              const errorMessages = Object.values(response.errors)
                .reduce((accumulator, value) => {
                  if (Array.isArray(value)) {
                    return accumulator.concat(value);
                  }
                  if (value) {
                    accumulator.push(value);
                  }
                  return accumulator;
                }, []);
              if (errorMessages.length > 0) {
                message = errorMessages.join(', ');
              }
            } else if (response.message) {
              message = response.message;
            }
          }
          showFormFeedback(message, 'error');
        });
    });

    toggleRemainingField();
  };

  $('.nav-links a').on('click', function (event) {
    event.preventDefault();
    const target = $(this).attr('href');

    $('.nav-links a').removeClass('active');
    $(this).addClass('active');

    $('html, body').animate(
      {
        scrollTop: $(target).offset().top - 24
      },
      500
    );
  });

  setupForm();
  loadDashboard();
});

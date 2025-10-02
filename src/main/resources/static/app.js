$(document).ready(function () {
  const formatCurrency = (value) =>
    new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL'
    }).format(value);

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
    $.getJSON('/api/finance/summary', (data) => {
      updateSummary(data.summary);
      renderExpenses(data.expenses);
      renderGoals(data.summary);
    });
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

  loadDashboard();
});
